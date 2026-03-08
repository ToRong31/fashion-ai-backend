package com.torome.store.product;

import com.torome.store.common.exception.ResourceNotFoundException;
import com.torome.store.product.dto.ProductListResponse;
import com.torome.store.product.dto.ProductResponse;
import com.torome.store.product.dto.VectorSearchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public ProductService(ProductRepository productRepository,
                          ElasticsearchOperations elasticsearchOperations) {
        this.productRepository = productRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Transactional(readOnly = true)
    public ProductListResponse vectorSearch(VectorSearchRequest request) {
        String query = request.query();
        int topK = request.effectiveTopK();

        try {
            List<ProductResponse> esResults = searchElasticsearch(query, topK);
            if (!esResults.isEmpty()) {
                log.debug("elasticsearch_search_hit count={}", esResults.size());
                return new ProductListResponse(esResults);
            }
        } catch (Exception e) {
            log.warn("elasticsearch_search_failed, falling back to DB: {}", e.getMessage());
        }

        List<ProductEntity> results = productRepository.searchByKeyword(query, topK);
        if (results.isEmpty()) {
            results = productRepository.findAll().stream().limit(topK).toList();
        }
        return new ProductListResponse(results.stream().map(ProductResponse::from).toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public ProductListResponse listProducts() {
        List<ProductResponse> products = productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
        return new ProductListResponse(products);
    }

    private List<ProductResponse> searchElasticsearch(String query, int topK) {
        Query nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(mm -> mm
                        .query(query)
                        .fields("name^3", "description^2", "category^2", "style", "color", "material", "gender")
                        .fuzziness("AUTO")
                ))
                .withMaxResults(topK)
                .build();

        SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);
        return hits.getSearchHits().stream()
                .map(hit -> ProductResponse.from(hit.getContent()))
                .toList();
    }
}

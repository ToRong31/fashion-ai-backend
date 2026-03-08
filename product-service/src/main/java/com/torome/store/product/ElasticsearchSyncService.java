package com.torome.store.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElasticsearchSyncService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchSyncService.class);

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;

    public ElasticsearchSyncService(ProductRepository productRepository,
                                    ProductSearchRepository productSearchRepository) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<ProductEntity> products = productRepository.findAll();
            List<ProductDocument> documents = products.stream()
                    .map(ProductDocument::from)
                    .toList();
            productSearchRepository.saveAll(documents);
            log.info("Indexed {} products to Elasticsearch", documents.size());
        } catch (Exception e) {
            log.warn("Failed to sync products to Elasticsearch on startup: {}. " +
                     "Search will fall back to database.", e.getMessage());
        }
    }
}

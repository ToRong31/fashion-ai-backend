package com.torome.store.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(@Value("${services.product.url}") String productUrl) {
        this.restClient = RestClient.builder().baseUrl(productUrl).build();
    }

    public List<ProductInfo> getProductsByIds(List<Long> ids) {
        Map<String, Object> response = restClient.post()
                .uri("/api/products/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(ids)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || !response.containsKey("products")) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) response.get("products");
        return products.stream()
                .map(p -> new ProductInfo(
                        ((Number) p.get("id")).longValue(),
                        (String) p.get("name"),
                        (String) p.get("description"),
                        ((Number) p.get("price")).doubleValue(),
                        p.get("stock_quantity") != null ? ((Number) p.get("stock_quantity")).intValue() : 0,
                        null
                ))
                .toList();
    }
}

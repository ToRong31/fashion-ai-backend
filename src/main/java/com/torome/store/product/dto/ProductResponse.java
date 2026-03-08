package com.torome.store.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.torome.store.product.ProductDocument;
import com.torome.store.product.ProductEntity;

import java.util.Map;

public record ProductResponse(
    Long id,
    String name,
    String description,
    double price,
    @JsonProperty("stock_quantity") int stockQuantity,
    Map<String, Object> metadata
) {
    public static ProductResponse from(ProductEntity entity) {
        return new ProductResponse(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPrice().doubleValue(),
            entity.getStockQuantity(),
            entity.getMetadata()
        );
    }

    public static ProductResponse from(ProductDocument doc) {
        return new ProductResponse(
            Long.parseLong(doc.getId()),
            doc.getName(),
            doc.getDescription(),
            doc.getPrice() != null ? doc.getPrice() : 0.0,
            doc.getStockQuantity() != null ? doc.getStockQuantity() : 0,
            doc.getMetadata()
        );
    }
}

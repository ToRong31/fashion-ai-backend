package com.torome.store.order.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ProductInfo(
    Long id,
    String name,
    String description,
    double price,
    @JsonProperty("stock_quantity") int stockQuantity,
    Map<String, Object> metadata
) {}

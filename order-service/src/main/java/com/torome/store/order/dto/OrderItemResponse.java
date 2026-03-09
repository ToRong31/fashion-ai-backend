package com.torome.store.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderItemResponse(
    @JsonProperty("product_id") Long productId,
    String name,
    double price,
    int quantity,
    String size
) {}

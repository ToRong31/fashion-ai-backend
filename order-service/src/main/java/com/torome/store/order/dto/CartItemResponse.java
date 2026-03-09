package com.torome.store.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CartItemResponse(
    Long id,
    @JsonProperty("user_id") Long userId,
    @JsonProperty("product_id") Long productId,
    @JsonProperty("product_name") String productName,
    double price,
    String size,
    int quantity,
    @JsonProperty("total_price") double totalPrice
) {}

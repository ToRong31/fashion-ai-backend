package com.torome.store.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
    @JsonProperty("user_id") @NotNull Long userId,
    @JsonProperty("product_id") @NotNull Long productId,
    String size,
    @Min(1) int quantity
) {}

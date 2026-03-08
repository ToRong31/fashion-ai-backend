package com.torome.store.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
    @JsonProperty("user_id") @NotNull Long userId,
    @JsonProperty("product_ids") @NotEmpty List<Long> productIds
) {}

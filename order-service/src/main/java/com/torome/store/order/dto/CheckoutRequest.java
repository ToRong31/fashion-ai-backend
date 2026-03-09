package com.torome.store.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
    @JsonProperty("user_id") @NotNull Long userId
) {}

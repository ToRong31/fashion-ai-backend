package com.torome.store.order.dto;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequest(
    @Min(0) int quantity,
    String size
) {}

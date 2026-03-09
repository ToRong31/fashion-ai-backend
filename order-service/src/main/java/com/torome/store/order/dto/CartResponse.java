package com.torome.store.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CartResponse(
    List<CartItemResponse> items,
    @JsonProperty("total_amount") double totalAmount
) {}

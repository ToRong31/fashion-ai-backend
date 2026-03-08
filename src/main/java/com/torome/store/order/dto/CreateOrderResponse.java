package com.torome.store.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CreateOrderResponse(
    Long id,
    @JsonProperty("user_id") Long userId,
    String status,
    @JsonProperty("total_amount") double totalAmount,
    @JsonProperty("product_ids") List<Long> productIds,
    @JsonProperty("vnpay_ref") String vnpayRef
) {}

package com.torome.store.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AutoCreateOrderResponse(
    Long id,
    @JsonProperty("user_id") Long userId,
    String status,
    @JsonProperty("total_amount") double totalAmount,
    List<OrderItemResponse> items,
    @JsonProperty("vnpay_ref") String vnpayRef
) {}

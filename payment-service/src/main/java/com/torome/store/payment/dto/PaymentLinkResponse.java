package com.torome.store.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentLinkResponse(
    @JsonProperty("order_id") Long orderId,
    @JsonProperty("payment_url") String paymentUrl
) {}

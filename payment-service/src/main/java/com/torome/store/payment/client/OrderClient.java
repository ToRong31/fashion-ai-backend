package com.torome.store.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OrderClient {

    private final RestClient restClient;

    public OrderClient(@Value("${services.order.url}") String orderUrl) {
        this.restClient = RestClient.builder().baseUrl(orderUrl).build();
    }

    public double getOrderAmount(Long orderId) {
        Map<String, Object> response = restClient.get()
                .uri("/api/orders/{id}", orderId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || !response.containsKey("total_amount")) {
            return 0.0;
        }
        return ((Number) response.get("total_amount")).doubleValue();
    }
}

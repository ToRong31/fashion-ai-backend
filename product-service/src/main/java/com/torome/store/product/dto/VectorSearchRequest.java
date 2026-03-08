package com.torome.store.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record VectorSearchRequest(
    String query,
    @JsonProperty("top_k") Integer topK
) {
    public int effectiveTopK() {
        return topK != null ? topK : 5;
    }
}

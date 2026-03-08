package com.torome.store.product.dto;

import java.util.List;

public record ProductListResponse(
    List<ProductResponse> products
) {}

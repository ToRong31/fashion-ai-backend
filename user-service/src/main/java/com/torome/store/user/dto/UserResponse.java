package com.torome.store.user.dto;

import java.util.Map;

public record UserResponse(
    Long id,
    String username,
    Map<String, Object> preferences
) {}

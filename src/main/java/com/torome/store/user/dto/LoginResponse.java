package com.torome.store.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
    @JsonProperty("user_id") Long userId,
    String username,
    String token
) {}

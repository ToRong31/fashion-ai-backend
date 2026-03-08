package com.torome.store.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record UpdateProfileRequest(
    @JsonProperty("user_id") @NotNull Long userId,
    @NotNull Map<String, Object> preferences
) {}

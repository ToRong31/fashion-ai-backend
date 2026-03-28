package com.torome.store.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Client for calling user-service to validate user existence.
 * Used to enforce cross-database referential integrity at the application layer
 * since PostgreSQL does not support FK constraints across databases.
 */
@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(@Value("${services.user.url}") String userUrl) {
        this.restClient = RestClient.builder().baseUrl(userUrl).build();
    }

    /**
     * Checks if a user exists by calling user-service.
     * @return true if the user exists (HTTP 200), false otherwise
     */
    public boolean userExists(Long userId) {
        try {
            restClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

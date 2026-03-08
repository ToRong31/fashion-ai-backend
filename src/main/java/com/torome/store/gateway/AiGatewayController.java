package com.torome.store.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiGatewayController {

    private final RestClient restClient;

    public AiGatewayController(
            @Value("${app.ai.orchestrator-url:http://localhost:8000}") String orchestratorUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(orchestratorUrl)
                .build();
    }

    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, Object> request) {
        return restClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {});
    }

    @GetMapping("/conversation/{userId}")
    public Map<String, Object> getConversation(@PathVariable String userId) {
        return restClient.get()
                .uri("/conversation/{userId}", userId)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {});
    }
}

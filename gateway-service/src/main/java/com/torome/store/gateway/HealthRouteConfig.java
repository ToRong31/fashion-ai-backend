package com.torome.store.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.Map;

@Configuration
public class HealthRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> healthRoute() {
        return RouterFunctions.route()
                .GET("/health", request ->
                        ServerResponse.ok().bodyValue(Map.of("status", "ok", "service", "gateway")))
                .build();
    }
}

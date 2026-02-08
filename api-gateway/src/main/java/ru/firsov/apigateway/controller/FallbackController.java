package ru.firsov.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "User Service is temporarily unavailable",
                        "timestamp", System.currentTimeMillis(),
                        "error", "Service Unavailable",
                        "status", 503
                )));
    }

    @GetMapping("/notification-service")
    public Mono<ResponseEntity<Map<String, Object>>> notificationServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Notification Service is temporarily unavailable",
                        "timestamp", System.currentTimeMillis(),
                        "error", "Service Unavailable",
                        "status", 503
                )));
    }
}
package ru.firsov.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.firsov.dto.NotificationRequestDTO;

@FeignClient(
        name = "notification-service",
        fallback = NotificationClientFallback.class
)
public interface NotificationClient {

    @PostMapping("/api/notifications/email")
    void sendEmail(@RequestBody NotificationRequestDTO request);
}
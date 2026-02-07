package ru.firsov.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.firsov.dto.NotificationRequestDTO;

@Slf4j
@Component
public class NotificationClientFallback implements NotificationClient {

    @Override
    public void sendEmail(NotificationRequestDTO request) {
        log.warn("Notification service is unavailable. Email to {} not sent",
                request.getToEmail());
    }
}
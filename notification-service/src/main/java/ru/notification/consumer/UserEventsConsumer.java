package ru.notification.consumer;

import ru.notification.dto.UserEventDTO;
import ru.notification.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventsConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topic.user-events}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserEvent(String message) {
        log.info("Получено сообщение из Kafka: {}", message);

        try {
            UserEventDTO event = objectMapper.readValue(message, UserEventDTO.class);

            switch (event.getEventType()) {
                case CREATE:
                    emailService.sendUserCreationEmail(event.getEmail(), event.getName());
                    break;
                case DELETE:
                    emailService.sendUserDeletionEmail(event.getEmail(), event.getName());
                    break;
                default:
                    log.warn("Неизвестный тип события: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщения: {}", e.getMessage(), e);
        }
    }
}
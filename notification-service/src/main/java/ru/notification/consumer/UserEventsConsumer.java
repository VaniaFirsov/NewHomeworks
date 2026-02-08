package ru.notification.consumer;

import ru.notification.dto.UserEventDTO;
import ru.notification.dto.EventType;
import ru.notification.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
// @RequiredArgsConstructor
public class UserEventsConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(UserEventsConsumer.class);

    public UserEventsConsumer(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

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
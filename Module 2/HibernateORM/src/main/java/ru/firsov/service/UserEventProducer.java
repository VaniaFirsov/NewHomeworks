package ru.firsov.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.firsov.dto.UserEventDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.user-events}")
    private String userEventsTopic;

    public void sendUserEvent(UserEventDTO event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(userEventsTopic, eventJson)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Сообщение отправлено в топик {}: {}", userEventsTopic, eventJson);
                        } else {
                            log.error("Ошибка при отправке сообщения в Kafka", ex);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации события в JSON", e);
        }
    }
}
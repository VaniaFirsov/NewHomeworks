package ru.firsov.service;

import ru.firsov.dto.EventType;
import ru.firsov.dto.UserEventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserEventProducer userEventProducer;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @Test
    void sendUserEvent_ShouldSerializeAndSendEvent() throws Exception {
        UserEventDTO event = new UserEventDTO(
                EventType.CREATE,
                "test@example.com",
                "Test User",
                1L
        );

        String expectedJson = "{\"eventType\":\"CREATE\",\"email\":\"test@example.com\",\"name\":\"Test User\",\"userId\":1}";

        when(objectMapper.writeValueAsString(event)).thenReturn(expectedJson);
        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        userEventProducer.sendUserEvent(event);

        verify(objectMapper, times(1)).writeValueAsString(event);
        verify(kafkaTemplate, times(1)).send(eq("user-events"), messageCaptor.capture());

        assertEquals(expectedJson, messageCaptor.getValue());
    }
}

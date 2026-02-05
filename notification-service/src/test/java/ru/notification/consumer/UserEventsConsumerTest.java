package ru.notification.consumer;

import ru.notification.dto.EventType;
import ru.notification.dto.UserEventDTO;
import ru.notification.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventsConsumerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserEventsConsumer userEventsConsumer;

    @Captor
    private ArgumentCaptor<String> emailCaptor;

    @Captor
    private ArgumentCaptor<String> nameCaptor;

    @Test
    void consumeUserEvent_ShouldSendWelcomeEmail_ForCreateEvent() throws Exception {
        String eventJson = "{\"eventType\":\"CREATE\",\"email\":\"test@example.com\",\"name\":\"Test User\",\"userId\":1}";
        UserEventDTO event = new UserEventDTO();
        event.setEventType(EventType.CREATE);
        event.setEmail("test@example.com");
        event.setName("Test User");
        event.setUserId(1L);

        when(objectMapper.readValue(eventJson, UserEventDTO.class)).thenReturn(event);

        userEventsConsumer.consumeUserEvent(eventJson);

        verify(emailService, times(1))
                .sendUserCreationEmail(emailCaptor.capture(), nameCaptor.capture());

        assertEquals("test@example.com", emailCaptor.getValue());
        assertEquals("Test User", nameCaptor.getValue());
    }

    @Test
    void consumeUserEvent_ShouldSendDeletionEmail_ForDeleteEvent() throws Exception {
        // Given
        String eventJson = "{\"eventType\":\"DELETE\",\"email\":\"test@example.com\",\"name\":\"Test User\",\"userId\":1}";
        UserEventDTO event = new UserEventDTO();
        event.setEventType(EventType.DELETE);
        event.setEmail("test@example.com");
        event.setName("Test User");
        event.setUserId(1L);

        when(objectMapper.readValue(eventJson, UserEventDTO.class)).thenReturn(event);

        // When
        userEventsConsumer.consumeUserEvent(eventJson);

        // Then
        verify(emailService, times(1))
                .sendUserDeletionEmail(emailCaptor.capture(), nameCaptor.capture());

        assertEquals("test@example.com", emailCaptor.getValue());
        assertEquals("Test User", nameCaptor.getValue());
    }
}
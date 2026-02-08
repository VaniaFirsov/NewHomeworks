package testutil;

import ru.firsov.User;
import ru.firsov.client.NotificationClient;
import ru.firsov.dto.EventType;
import ru.firsov.dto.NotificationRequestDTO;
import ru.firsov.dto.UserEventDTO;
import ru.firsov.dto.UserRequestDTO;
import ru.firsov.dto.UserResponseDTO;
import ru.firsov.model.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import ru.firsov.service.UserEventProducer;
import ru.firsov.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventProducer userEventProducer;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<UserEventDTO> eventCaptor;

    @Captor
    private ArgumentCaptor<NotificationRequestDTO> notificationCaptor;

    @Test
    void createUser_ShouldSaveUser() {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setName("Test User");
        requestDTO.setEmail("test@example.com");
        requestDTO.setAge(25);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setAge(25);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doNothing().when(userEventProducer).sendUserEvent(any(UserEventDTO.class));
        doNothing().when(notificationClient).sendEmail(any(NotificationRequestDTO.class));

        UserResponseDTO result = userService.createUser(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(25, result.getAge());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userEventProducer, times(1)).sendUserEvent(eventCaptor.capture());
        verify(notificationClient, times(1)).sendEmail(notificationCaptor.capture());

        UserEventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(EventType.CREATE, capturedEvent.getEventType());
        assertEquals("test@example.com", capturedEvent.getEmail());
        assertEquals("Test User", capturedEvent.getName());
        assertEquals(1L, capturedEvent.getUserId());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        UserRequestDTO requestDTO = new UserRequestDTO();
        requestDTO.setName("Test User");
        requestDTO.setEmail("existing@example.com");
        requestDTO.setAge(25);

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(requestDTO));

        assertEquals("Пользователь с email existing@example.com уже существует",
                exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(userEventProducer, never()).sendUserEvent(any());
        verify(notificationClient, never()).sendEmail(any());
    }
}
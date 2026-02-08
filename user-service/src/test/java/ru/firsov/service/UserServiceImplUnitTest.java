package ru.firsov.service;

import ru.firsov.User;
import ru.firsov.client.NotificationClient;
import ru.firsov.dto.EventType;
import ru.firsov.dto.NotificationRequestDTO;
import ru.firsov.dto.UserEventDTO;
import ru.firsov.dto.UserRequestDTO;
import ru.firsov.dto.UserResponseDTO;
import ru.firsov.exception.UserNotFoundException;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserEventProducer userEventProducer;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<UserEventDTO> eventCaptor;

    @Captor
    private ArgumentCaptor<NotificationRequestDTO> notificationCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void createUser_ShouldSaveUserAndSendEvents() {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setAge(25);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@example.com");
        savedUser.setAge(25);
        savedUser.setCreatedAt(LocalDateTime.now());

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doNothing().when(userEventProducer).sendUserEvent(any(UserEventDTO.class));
        doNothing().when(notificationClient).sendEmail(any(NotificationRequestDTO.class));

        UserResponseDTO result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(25, result.getAge());

        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(userEventProducer, times(1)).sendUserEvent(eventCaptor.capture());
        verify(notificationClient, times(1)).sendEmail(notificationCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("Test User", capturedUser.getName());
        assertEquals("test@example.com", capturedUser.getEmail());

        UserEventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(EventType.CREATE, capturedEvent.getEventType());
        assertEquals("test@example.com", capturedEvent.getEmail());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Test User");
        request.setEmail("existing@example.com");
        request.setAge(25);

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Пользователь с email existing@example.com уже существует",
                exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(userEventProducer, never()).sendUserEvent(any());
        verify(notificationClient, never()).sendEmail(any());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUser_ShouldDeleteAndSendEvent() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(25);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userId);
        doNothing().when(userEventProducer).sendUserEvent(any(UserEventDTO.class));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
        verify(userEventProducer, times(1)).sendUserEvent(eventCaptor.capture());

        UserEventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(EventType.DELETE, capturedEvent.getEventType());
        assertEquals("test@example.com", capturedEvent.getEmail());
        assertEquals(userId, capturedEvent.getUserId());
    }
}
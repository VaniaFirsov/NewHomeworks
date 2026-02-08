package ru.firsov.service;

import ru.firsov.User;
import ru.firsov.dto.UserRequestDTO;
import ru.firsov.model.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")
    );

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_ShouldSendKafkaEvent() throws Exception {
        UserRequestDTO request = new UserRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setAge(25);

        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        userService.createUser(request);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(kafkaTemplate, times(1)).send(eq("user-events"), anyString());
        });

        Optional<User> savedUser = userRepository.findByEmail("test@example.com");
        assertTrue(savedUser.isPresent());
    }

    @Test
    void deleteUser_ShouldSendKafkaEvent() throws Exception {
        User user = new User();
        user.setName("Test User");
        user.setEmail("delete@example.com");
        user.setAge(30);
        User savedUser = userRepository.save(user);

        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        userService.deleteUser(savedUser.getId());

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(kafkaTemplate, times(1)).send(eq("user-events"), anyString());
        });

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertFalse(deletedUser.isPresent());
    }
}
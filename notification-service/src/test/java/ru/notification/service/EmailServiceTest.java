package ru.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @Test
    void sendSimpleMessage_ShouldSendEmail() {
        emailService.sendSimpleMessage(
                "test@example.com",
                "Test Subject",
                "Test Message"
        );

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals("Test Subject", sentMessage.getSubject());
        assertEquals("Test Message", sentMessage.getText());
    }

    @Test
    void sendUserCreationEmail_ShouldSendWelcomeEmail() {
        emailService.sendUserCreationEmail("test@example.com", "Test User");

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals("Добро пожаловать!", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("Test User"));
        assertTrue(sentMessage.getText().contains("Ваш аккаунт на сайте ваш сайт был успешно создан"));
    }

    @Test
    void sendUserDeletionEmail_ShouldSendDeletionEmail() {
        emailService.sendUserDeletionEmail("test@example.com", "Test User");

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals("Ваш аккаунт удален", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("Test User"));
        assertTrue(sentMessage.getText().contains("Ваш аккаунт был удалён"));
    }
}
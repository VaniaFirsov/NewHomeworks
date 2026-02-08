package ru.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailRequestDTO {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String toEmail;

    @NotBlank(message = "Тема обязательна")
    private String subject;

    @NotBlank(message = "Сообщение обязательно")
    private String message;

    public String getToEmail() {
        return toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
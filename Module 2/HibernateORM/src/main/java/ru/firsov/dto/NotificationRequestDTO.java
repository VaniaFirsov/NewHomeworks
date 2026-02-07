package ru.firsov.dto;

import lombok.Data;

@Data
public class NotificationRequestDTO {
    private String toEmail;
    private String subject;
    private String message;
}
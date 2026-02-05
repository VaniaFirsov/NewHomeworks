package ru.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class UserEventDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType; // Тот же Enum

    private String email;
    private String name;
    private Long userId;
}
package ru.firsov.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class UserEventDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType;

    private String email;
    private String name;
    private Long userId;

    public UserEventDTO() {}

    public UserEventDTO(EventType eventType, String email, String name, Long userId) {
        this.eventType = eventType;
        this.email = email;
        this.name = name;
        this.userId = userId;
    }
}
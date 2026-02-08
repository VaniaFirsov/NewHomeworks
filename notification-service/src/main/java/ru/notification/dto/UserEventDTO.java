package ru.notification.dto;

public class UserEventDTO {
    private EventType eventType;
    private String email;
    private String name;
    private Long userId;

    public EventType getEventType() {
        return eventType;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
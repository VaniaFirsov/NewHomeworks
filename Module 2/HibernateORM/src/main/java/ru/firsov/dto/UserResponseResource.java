package ru.firsov.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Getter
@Relation(collectionRelation = "users", itemRelation = "user")
public class UserResponseResource extends RepresentationModel<UserResponseResource> {

    @JsonProperty("id")
    private final Long userId;

    private final String name;
    private final String email;
    private final Integer age;
    private final LocalDateTime createdAt;

    public UserResponseResource(UserResponseDTO dto) {
        this.userId = dto.getId();
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.age = dto.getAge();
        this.createdAt = dto.getCreatedAt();
    }
}
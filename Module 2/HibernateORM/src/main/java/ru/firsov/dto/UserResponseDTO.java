package ru.firsov.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Ответ с информацией о пользователе")
public class UserResponseDTO {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;

    @Schema(description = "Email пользователя", example = "ivan@example.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "30")
    private Integer age;

    @Schema(description = "Дата и время создания пользователя", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
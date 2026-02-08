package ru.firsov.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание/обновление пользователя")
public class UserRequestDTO {

    @Schema(
            description = "Имя пользователя",
            example = "Иван Иванов",
            minLength = 2,
            maxLength = 100
    )
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String name;

    @Schema(
            description = "Email пользователя",
            example = "ivan@example.com"
    )
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(
            description = "Возраст пользователя",
            example = "30",
            minimum = "0",
            maximum = "150"
    )
    private Integer age;
}
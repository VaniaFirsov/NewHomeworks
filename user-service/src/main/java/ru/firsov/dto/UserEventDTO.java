package ru.firsov.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Событие пользователя для Kafka")
public class UserEventDTO {

    @Schema(description = "Тип события", example = "CREATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private EventType eventType;

    @Schema(description = "Email пользователя", example = "ivan@example.com")
    private String email;

    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;

    @Schema(description = "ID пользователя", example = "1")
    private Long userId;
}
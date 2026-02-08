package ru.firsov.controller;

import org.springframework.hateoas.IanaLinkRelations;
import ru.firsov.assembler.UserResourceAssembler;
import ru.firsov.dto.UserRequestDTO;
import ru.firsov.dto.UserResponseDTO;
import ru.firsov.dto.UserResponseResource;
import ru.firsov.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;
    private final UserResourceAssembler userResourceAssembler;

    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя, сохраняет в БД и отправляет событие в Kafka для email-уведомления"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно создан",
                    content = @Content(schema = @Schema(implementation = UserResponseResource.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные пользователя или email уже существует"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    @PostMapping
    public ResponseEntity<UserResponseResource> createUser(
            @Parameter(description = "Данные пользователя для создания", required = true)
            @Valid @RequestBody UserRequestDTO userRequestDTO) {

        log.info("POST /api/users - создание пользователя");
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        UserResponseResource resource = userResourceAssembler.toModel(createdUser);

        resource.add(linkTo(methodOn(UserController.class).getAllUsers())
                .withRel(IanaLinkRelations.COLLECTION));

        return ResponseEntity
                .created(resource.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(resource);
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает информацию о пользователе по его идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден",
                    content = @Content(schema = @Schema(implementation = UserResponseResource.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseResource> getUserById(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long id) {

        log.info("GET /api/users/{} - получение пользователя", id);
        UserResponseDTO user = userService.getUserById(id);
        UserResponseResource resource = userResourceAssembler.toModel(user);

        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей с пагинацией"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список пользователей успешно получен",
                    content = @Content(schema = @Schema(implementation = CollectionModel.class))
            )
    })
    @GetMapping
    public ResponseEntity<CollectionModel<UserResponseResource>> getAllUsers() {

        log.info("GET /api/users - получение всех пользователей");
        List<UserResponseDTO> users = userService.getAllUsers();
        CollectionModel<UserResponseResource> resources =
                userResourceAssembler.toCollectionModel(users);

        resources.add(linkTo(methodOn(UserController.class).getAllUsers())
                .withRel(IanaLinkRelations.SELF));
        resources.add(linkTo(methodOn(UserController.class).createUser(null))
                .withRel("create"));

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Обновить пользователя",
            description = "Обновляет информацию о существующем пользователе"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно обновлен",
                    content = @Content(schema = @Schema(implementation = UserResponseResource.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные пользователя"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseResource> updateUser(
            @Parameter(description = "ID пользователя для обновления", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @Valid @RequestBody UserRequestDTO userRequestDTO) {

        log.info("PUT /api/users/{} - обновление пользователя", id);
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        UserResponseResource resource = userResourceAssembler.toModel(updatedUser);

        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по ID и отправляет событие в Kafka для email-уведомления"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Пользователь успешно удален"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя для удаления", required = true, example = "1")
            @PathVariable Long id) {

        log.info("DELETE /api/users/{} - удаление пользователя", id);
        userService.deleteUser(id);

        return ResponseEntity.noContent()
                .header("Link", linkTo(methodOn(UserController.class).getAllUsers())
                        .withRel(IanaLinkRelations.COLLECTION).getHref())
                .build();
    }
}
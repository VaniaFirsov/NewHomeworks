package ru.firsov.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Корень API", description = "Корневой endpoint для навигации по API")
public class ApiRootController {

    @Operation(
            summary = "Получить корневые ссылки API",
            description = "Возвращает доступные endpoints API с HATEOAS ссылками"
    )
    @GetMapping
    public ResponseEntity<EntityModel<Map<String, String>>> getApiRoot() {
        Map<String, String> apiInfo = new HashMap<>();
        apiInfo.put("name", "User Service API");
        apiInfo.put("version", "1.0.0");
        apiInfo.put("description", "Микросервис для управления пользователями");

        EntityModel<Map<String, String>> resource = EntityModel.of(apiInfo);

        resource.add(linkTo(methodOn(ApiRootController.class).getApiRoot()).withSelfRel());
        resource.add(linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
        resource.add(linkTo(methodOn(UserController.class).createUser(null)).withRel("create-user"));

        resource.add(linkTo(UserController.class).slash("swagger-ui.html").withRel("api-docs"));

        return ResponseEntity.ok(resource);
    }
}
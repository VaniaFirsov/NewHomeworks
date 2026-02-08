package ru.firsov.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Локальный сервер"),
                        new Server()
                                .url("http://user-service:8080")
                                .description("Сервер в Docker")
                ))
                .info(new Info()
                        .title("User Service API")
                        .version("1.0.0")
                        .description("Микросервис для управления пользователями с интеграцией Kafka и email-уведомлениями")
                        .contact(new Contact()
                                .name("Поддержка")
                                .email("support@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                        .termsOfService("https://example.com/terms")
                );
    }
}

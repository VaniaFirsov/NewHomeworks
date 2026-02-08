FROM openjdk:17-jdk-slim

# Устанавливаем curl для healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Копируем JAR файл (собираем заранее)
COPY target/config-server-*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
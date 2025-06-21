# Этап сборки
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# Копируем файлы конфигурации Gradle для лучшего использования кэша
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./

# Копируем openapi.yaml для генерации API классов
COPY openapi.yaml ./

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN gradle build -x test

# Этап выполнения
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Копируем JAR из этапа сборки
COPY --from=builder /app/build/libs/*.jar app.jar

# Переменные окружения для подключения к БД
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/person
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 
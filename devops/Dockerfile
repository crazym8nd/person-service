# Этап сборки
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
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
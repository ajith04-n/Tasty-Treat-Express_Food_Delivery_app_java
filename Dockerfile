# Stage 1: Build the Spring Boot backend
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy backend code (folder name must match repo exactly)
COPY tasty-treat-express-backend /app

# Build the backend project and skip tests
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the backend port
EXPOSE 8080

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]

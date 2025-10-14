# Stage 1: Build the Spring Boot backend
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only backend code (adjust folder name to match your repo)
COPY tasty-treat-express-backend /app

# Build the backend project
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]

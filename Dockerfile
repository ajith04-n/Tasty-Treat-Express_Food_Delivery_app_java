# Use the official OpenJDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the project files
COPY . .

# Give execute permission for Maven Wrapper
RUN chmod +x mvnw

# Build the project (skip tests to save time)
RUN ./mvnw clean package -DskipTests

# Expose port 8080 for Render
EXPOSE 8080

# Run the jar file (Spring Boot)
CMD ["java", "-jar", "target/*.jar"]

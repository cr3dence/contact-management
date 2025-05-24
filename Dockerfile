# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy full source and build
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose app port
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
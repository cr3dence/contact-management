# Use lightweight JDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the built jar into the container
COPY target/contractmanagement-0.0.1-SNAPSHOT.jar contact.jar

# Expose application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "contact.jar"]

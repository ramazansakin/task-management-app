# Dockerfile for Spring Boot App with JDK 17
FROM openjdk:17-jdk-slim

# Copy the packaged Spring Boot application JAR file into the container
COPY target/*.jar app.jar

# Expose the port your Spring Boot application runs on (typically 8080)
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app.jar"]
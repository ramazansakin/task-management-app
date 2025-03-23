# Task Management API

A simple task management API built with Spring Boot and Java 21.

## Overview

This application provides REST endpoints to create, retrieve, update, and delete tasks.

## Tech Stack

* Java 21
* Spring Boot
* OpenAPI/Swagger for documentation

## Getting Started

### Prerequisites

* JDK 21
* Maven or Gradle

### Running the Application

```bash
# Using Maven
mvn spring-boot:run

# Using Gradle
gradle bootRun
```

The application runs at `http://localhost:8080`.

## API Documentation

Full API documentation is available via Swagger UI:

Access the Swagger UI: `http://localhost:8080/swagger-ui.html`

## Main Endpoints

* `POST /api/tasks` - Create a new task
* `GET /api/tasks` - Get all tasks
* `GET /api/tasks/{id}` - Get task by ID
* `PATCH /api/tasks/{id}/status` - Update task status
* `DELETE /api/tasks/{id}` - Delete a task
* `GET /api/tasks/status/{status}` - Get tasks by status
* `GET /api/tasks/title/{title}` - Find task by title

## Configuration

Basic configuration can be adjusted in `application.properties` or `application.yml`.
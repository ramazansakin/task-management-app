package tr.com.rsakin.taskmanagementapp.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

// Entity class
// Clean code: Immutability principle
// Clean code: Builder pattern
public class Task {
    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enums should be placed in their own files in a real project
    public enum TaskStatus {
        TODO, IN_PROGRESS, DONE
    }

    // Clean code: Builder pattern for better readability and immutability
    public static class Builder {
        private final Task task;

        public Builder() {
            task = new Task();
            task.id = UUID.randomUUID();
            task.status = TaskStatus.TODO;
            task.createdAt = LocalDateTime.now();
            task.updatedAt = LocalDateTime.now();
        }

        public Builder withTitle(String title) {
            task.title = title;
            return this;
        }

        public Builder withDescription(String description) {
            task.description = description;
            return this;
        }

        public Task build() {
            return task;
        }
    }

    // Private constructor to enforce the builder pattern
    private Task() {}

    // Getters (Clean code: Immutability principle)
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Only the status can be updated
    public Task updateStatus(TaskStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

}

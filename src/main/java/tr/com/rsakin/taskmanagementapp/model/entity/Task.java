package tr.com.rsakin.taskmanagementapp.model.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

// Entity class
// Clean code: Immutability principle
// Clean code: Builder pattern

@Getter
@Builder
public class Task {
    private final UUID id;
    private final String title;
    private final String description;
    @With // Lombok's @With creates immutable "with" methods
    private final TaskStatus status;
    private final LocalDateTime createdAt;
    @With
    private final TaskPriority priority;

    // Lombok builder with default values
    public static class TaskBuilder {
        private UUID id = UUID.randomUUID();
        private TaskStatus status = TaskStatus.PENDING;
        private LocalDateTime createdAt = LocalDateTime.now();
        private TaskPriority priority = new LowPriority();
    }

    // Immutable pattern using Lombok's @With
    public Task updateStatus(TaskStatus newStatus) {
        return this.withStatus(newStatus);
    }

    // Using Java 17 sealed classes for task status
    public enum TaskStatus {
        PENDING, IN_PROGRESS, BLOCKED, COMPLETED
    }

    // Java 17 sealed classes hierarchy for task priorities
    public sealed interface TaskPriority permits LowPriority, MediumPriority, HighPriority {
        String getLabel();
        int getValue();
    }

    public static final class LowPriority implements TaskPriority {
        @Override
        public String getLabel() {
            return "Low";
        }

        @Override
        public int getValue() {
            return 1;
        }
    }

    public static final class MediumPriority implements TaskPriority {
        @Override
        public String getLabel() {
            return "Medium";
        }

        @Override
        public int getValue() {
            return 2;
        }
    }

    public static final class HighPriority implements TaskPriority {
        @Override
        public String getLabel() {
            return "High";
        }

        @Override
        public int getValue() {
            return 3;
        }
    }
}

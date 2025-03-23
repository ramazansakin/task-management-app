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
    private final Priority priority;

    // Lombok builder with default values
    public static class TaskBuilder {
        private UUID id = UUID.randomUUID();
        private TaskStatus status = TaskStatus.PENDING;
        private LocalDateTime createdAt = LocalDateTime.now();
        private Priority priority = new LowPriority();
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
    public sealed interface Priority permits LowPriority, MediumPriority, HighPriority {
        String getLabel();
        int getValue();
    }

    public enum TaskPriority {
        LOW( 1, "Low"),
        MEDIUM(2, "Medium"),
        HIGH(3, "High");

        private final int value;
        private final String label;

        TaskPriority(int value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    public static final class LowPriority implements Priority {
        @Override
        public String getLabel() {
            return TaskPriority.LOW.label;
        }

        @Override
        public int getValue() {
            return TaskPriority.LOW.value;
        }
    }

    public static final class MediumPriority implements Priority {
        @Override
        public String getLabel() {
            return TaskPriority.MEDIUM.label;
        }

        @Override
        public int getValue() {
            return TaskPriority.MEDIUM.value;
        }
    }

    public static final class HighPriority implements Priority {
        @Override
        public String getLabel() {
            return TaskPriority.HIGH.label;
        }

        @Override
        public int getValue() {
            return TaskPriority.HIGH.value;
        }
    }

}

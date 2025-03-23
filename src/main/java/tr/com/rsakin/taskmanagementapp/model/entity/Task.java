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
    private final TaskStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Immutable pattern - returns a new instance
    public Task updateStatus(TaskStatus newStatus) {
        return Task.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .status(newStatus)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
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

package tr.com.rsakin.taskmanagementapp.model.dto.response;

import tr.com.rsakin.taskmanagementapp.model.entity.Task;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponseDTO(
        UUID id,
        String title,
        String description,
        Task.TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

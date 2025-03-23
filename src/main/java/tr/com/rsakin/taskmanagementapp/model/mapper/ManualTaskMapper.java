package tr.com.rsakin.taskmanagementapp.model.mapper;

import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;

public class ManualTaskMapper {

    private ManualTaskMapper() {}

    public static TaskResponseDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getPriority()
        );
    }

}

package tr.com.rsakin.taskmanagementapp.service;

import org.springframework.stereotype.Service;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;
import tr.com.rsakin.taskmanagementapp.model.mapper.TaskMapperManuel;
import tr.com.rsakin.taskmanagementapp.model.mapper.TaskResponseMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {
    // In-memory storage (will replace with DB in later weeks)
    private final Map<UUID, Task> taskStore = new ConcurrentHashMap<>();
    private final TaskResponseMapper taskResponseMapper = TaskResponseMapper.INSTANCE;


    public TaskResponseDTO createTask(String title, String description) {
        validateTaskInput(title, description);

        Task task = new Task.Builder()
                .withTitle(title)
                .withDescription(description)
                .build();

        taskStore.put(task.getId(), task);
        return TaskMapperManuel.toDTO(task); // Manual mapping here
    }

    public List<TaskResponseDTO> getAllTasks() {
        return taskResponseMapper.toDTOList(new ArrayList<>(taskStore.values())); // Using MapStruct
    }

    public TaskResponseDTO getTaskById(UUID id) {
        Task task = taskStore.get(id);
        return taskResponseMapper.toDTO(task); // Using MapStruct
    }

    public Task updateTaskStatus(UUID id, Task.TaskStatus newStatus) {
        Task task = taskStore.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }

        Task updatedTask = task.updateStatus(newStatus);
        taskStore.put(id, updatedTask);
        return updatedTask;
    }

    public void deleteTask(UUID id) {
        taskStore.remove(id);
    }

    // Clean code: Validation extracted to a separate method
    private void validateTaskInput(String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        if (description == null) {
            throw new IllegalArgumentException("Task description cannot be null");
        }
    }
}

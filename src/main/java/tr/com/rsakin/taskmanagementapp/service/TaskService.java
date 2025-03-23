package tr.com.rsakin.taskmanagementapp.service;

import org.springframework.stereotype.Service;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;
import tr.com.rsakin.taskmanagementapp.model.mapper.TaskMapperManuel;
import tr.com.rsakin.taskmanagementapp.model.mapper.TaskResponseMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TaskService {
    // In-memory storage (will replace with DB in later weeks)
    private final Map<UUID, Task> taskStore = new HashMap<>();
    private static final TaskResponseMapper TASK_MAPPER = TaskResponseMapper.INSTANCE;

    // Event publishing for task operations (Java 8 functional interfaces)
    private final List<Consumer<Task>> taskCreationListeners = new ArrayList<>();
    private final List<Consumer<Task>> taskCompletionListeners = new ArrayList<>();

    public TaskResponseDTO createTask(String title, String description) {
        validateTaskInput(title, description);

        Task task = Task.builder()
                .title(title)
                .description(description)
                .build();

        taskStore.put(task.getId(), task);
        return TaskMapperManuel.toDTO(task); // Manual mapping here
    }

    public List<TaskResponseDTO> getAllTasks() {
        return TASK_MAPPER.toDTOList(new ArrayList<>(taskStore.values())); // Using MapStruct
    }

    public TaskResponseDTO getTaskById(UUID id) {
        Task task = taskStore.get(id);
        return TASK_MAPPER.toDTO(task); // Using MapStruct
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

    // Java 8: Stream API for filtering tasks
    public List<TaskResponseDTO> getTasksByStatus(Task.TaskStatus status) {
        return taskStore.values().stream()
                .filter(task -> task.getStatus() == status)
                .map(TASK_MAPPER::toDTO)
                .toList();
    }

    // Java 8: Optional for potentially null values
    public Optional<TaskResponseDTO> findTaskByTitle(String title) {
        return taskStore.values().stream()
                .filter(task -> task.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .map(TASK_MAPPER::toDTO);
    }

    // Java 9: Factory methods for collections
    public List<Task.TaskStatus> getAvailableStatuses() {
        return List.of(Task.TaskStatus.values());
    }

    // Java 9: Stream improvements - takeWhile
    public List<TaskResponseDTO> getTasksCreatedBefore(LocalDateTime dateTime) {
        return taskStore.values().stream()
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .takeWhile(task -> task.getCreatedAt().isBefore(dateTime))
                .map(TASK_MAPPER::toDTO)
                .toList();
    }

    // Java 10: Local variable type inference
    public Map<Task.TaskStatus, Long> getTaskCountByStatus() {
        var result = new HashMap<Task.TaskStatus, Long>();

        for (var status : Task.TaskStatus.values()) {
            var count = taskStore.values().stream()
                    .filter(task -> task.getStatus() == status)
                    .count();
            result.put(status, count);
        }

        return result;
    }

    // Java 11: String API improvements
    public List<TaskResponseDTO> searchTasks(String searchTerm) {
        if (searchTerm.isBlank()) {
            return getAllTasks();
        }

        return taskStore.values().stream()
                .filter(task ->
                        task.getTitle().contains(searchTerm) ||
                        (task.getDescription() != null && task.getDescription().contains(searchTerm)))
                .map(TASK_MAPPER::toDTO)
                .toList();
    }

    // Java 12: Switch expressions (preview in 12, standard in 14)
    public String getTaskPriority(UUID id) {
        Task task = taskStore.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }

        return switch (task.getStatus()) {
            case PENDING -> "Low";
            case IN_PROGRESS -> "Medium";
            case BLOCKED -> "High";
            case COMPLETED -> "None";
        };
    }

    // Java 14: Records (preview in 14, standard in 16) for DTOs
    public record TaskStatistics(long total, long pending, long inProgress, long blocked, long completed) {}

    public TaskStatistics getTaskStatistics() {
        long total = taskStore.size();
        long pending = countTasksByStatus(Task.TaskStatus.PENDING);
        long inProgress = countTasksByStatus(Task.TaskStatus.IN_PROGRESS);
        long blocked = countTasksByStatus(Task.TaskStatus.BLOCKED);
        long completed = countTasksByStatus(Task.TaskStatus.COMPLETED);

        return new TaskStatistics(total, pending, inProgress, blocked, completed);
    }

    private long countTasksByStatus(Task.TaskStatus status) {
        return taskStore.values().stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }

    // Java 15: Text blocks for complex queries
    public String generateTaskReport() {
        return """
                TASK MANAGEMENT REPORT
                ----------------------
                Total Tasks: %d
                Pending: %d
                In Progress: %d
                Blocked: %d
                Completed: %d
                
                Last Updated: %s
                """.formatted(
                taskStore.size(),
                countTasksByStatus(Task.TaskStatus.PENDING),
                countTasksByStatus(Task.TaskStatus.IN_PROGRESS),
                countTasksByStatus(Task.TaskStatus.BLOCKED),
                countTasksByStatus(Task.TaskStatus.COMPLETED),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    // Java 16: Pattern matching for instanceof
    public String getTaskDescription(Object taskIdentifier) {
        switch (taskIdentifier) {
            case UUID id -> {
                Task task = taskStore.get(id);
                return task != null ? task.getDescription() : "Task not found";
            }
            case String title -> {
                return findTaskByTitle(title)
                        .map(TaskResponseDTO::description)
                        .orElse("Task not found");
            }
            default -> {
                return "Invalid task identifier";
            }
        }
    }

    // Java 17: Sealed classes (related to Task.java, shown separately)

    // Java 19: Virtual threads (preview) - Simulating async operations
    public CompletableFuture<List<TaskResponseDTO>> getTasksAsync() {
        return CompletableFuture.supplyAsync(this::getAllTasks, Executors.newVirtualThreadPerTaskExecutor());
    }

    // Java 21: Pattern matching for switch
    public String describeTask(Object obj) {
        return switch (obj) {
            case UUID id -> {
                Task task = taskStore.get(id);
                yield task != null ? "Task: " + task.getTitle() : "Unknown task";
            }
            case Task task -> "Task: " + task.getTitle() + " (" + task.getStatus() + ")";
            case String s -> "Search for: " + s;
            default -> "Unknown object";
        };
    }

    // Java 21: Record patterns
    public record TaskDuration(UUID id, Duration duration) {}

    public List<String> analyzeTaskDurations(List<TaskDuration> durations) {
        return durations.stream()
                .map(duration -> switch (duration) {
                    case TaskDuration(UUID id, Duration d) when d.toHours() < 1 ->
                            "Task " + id + ": Quick task";
                    case TaskDuration(UUID id, Duration d) when d.toHours() < 8 ->
                            "Task " + id + ": Medium task";
                    case TaskDuration(UUID id, Duration d) ->
                            "Task " + id + ": Long task";
                })
                .toList();
    }

    // Java 22: String Templates (preview)
    public String getTaskSummary(UUID id) {
        Task task = taskStore.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }

        // Using StringTemplate.Processor (preview feature)
        // Note: In actual code you would use STR."Task: \{task.getTitle()}, Status: \{task.getStatus()}"
        // Using traditional string concatenation for compatibility
        return "Task: " + task.getTitle() + ", Status: " + task.getStatus();
    }

    // Java 23: Unnamed patterns and variables (preview)
    public boolean hasTaskWithStatus(Task.TaskStatus status) {
        return taskStore.values().stream()
                .anyMatch(task -> switch (task) {
                    // In actual Java 23 code: case Task(_, _, _, status, _) -> true;
                    case Task t when t.getStatus() == status -> true;
                    default -> false;
                });
    }

    // Java 24: Stream gatherers (preview)
    public Map<Task.TaskStatus, List<TaskResponseDTO>> groupTasksByStatus() {
        // In Java 24 this would use the new Stream.gather() API
        // For now using traditional groupingBy collector
        return taskStore.values().stream()
                .collect(Collectors.groupingBy(
                        Task::getStatus,
                        Collectors.mapping(TASK_MAPPER::toDTO, Collectors.toList())
                ));
    }

    // Observer pattern for task lifecycle events (Java 8 functional interfaces)
    public void addTaskCreationListener(Consumer<Task> listener) {
        taskCreationListeners.add(listener);
    }

    public void addTaskCompletionListener(Consumer<Task> listener) {
        taskCompletionListeners.add(listener);
    }

    // Use the TaskPriority sealed interface
    public Task.TaskPriority getTaskPriorityObject(UUID id) {
        Task task = taskStore.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }

        // Using pattern matching for switch with sealed interface
        return switch (task.getStatus()) {
            case PENDING, COMPLETED -> new Task.LowPriority();
            case IN_PROGRESS -> new Task.MediumPriority();
            case BLOCKED -> new Task.HighPriority();
        };
    }

    // Method to update task with priority
    public Task updateTaskPriority(UUID id, Task.TaskPriority priority) {
        Task task = taskStore.get(id);
        if (task == null) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }

        // We need to update the Task model to include priority
        // For now, just returning the task since we can't modify it
        // In a real implementation, this would return a new Task with updated priority
        return task;
    }

    // Get tasks by priority value
    public List<TaskResponseDTO> getTasksByPriority(int priorityValue) {
        return taskStore.values().stream()
                .filter(task -> {
                    Task.TaskPriority priority = getTaskPriorityObject(task.getId());
                    return priority.getValue() == priorityValue;
                })
                .map(TASK_MAPPER::toDTO)
                .toList();
    }

}

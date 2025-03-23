package tr.com.rsakin.taskmanagementapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskStatistics;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;
import tr.com.rsakin.taskmanagementapp.model.mapper.ManualTaskMapper;
import tr.com.rsakin.taskmanagementapp.model.mapper.TaskResponseMapper;
import tr.com.rsakin.taskmanagementapp.repository.TaskRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    // Event publishing for task operations (Java 8 functional interfaces)
    private final List<Consumer<Task>> taskCreationListeners = new ArrayList<>();
    private final List<Consumer<Task>> taskCompletionListeners = new ArrayList<>();

    @Transactional
    public TaskResponseDTO createTask(String title, String description) {
        validateTaskInput(title, description);

        Task task = Task.builder()
                .title(title)
                .description(description)
                .build();

        Task savedTask = taskRepository.save(task);

        // Notify creation listeners
        taskCreationListeners.forEach(listener -> listener.accept(savedTask));

        return ManualTaskMapper.toDTO(savedTask);
    }

    public List<TaskResponseDTO> getAllTasks() {
        return TaskResponseMapper.INSTANCE.toDTOList(taskRepository.findAll());
    }

    public TaskResponseDTO getTaskById(UUID id) {
        return taskRepository.findById(id)
                .map(TaskResponseMapper.INSTANCE::toDTO)
                .orElse(null);
    }

    @Transactional
    public Task updateTaskStatus(UUID id, Task.TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + id));

        if (task.getStatus() == Task.TaskStatus.BLOCKED)
            throw new TaskStatusNotAvailableException("Task not found with ID: " + id);

        Task updatedTask = task.updateStatus(newStatus);
        Task savedTask = taskRepository.save(updatedTask);

        // Notify completion listeners if task is completed
        if (newStatus == Task.TaskStatus.COMPLETED) {
            taskCompletionListeners.forEach(listener -> listener.accept(savedTask));
        }

        return savedTask;
    }

    @Transactional
    public void deleteTask(UUID id) {
        taskRepository.deleteById(id);
    }

    // Validation
    private void validateTaskInput(String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        if (description == null) {
            throw new IllegalArgumentException("Task description cannot be null");
        }
    }

    // Methods using JPA standard repository methods

    public List<TaskResponseDTO> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(TaskResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<TaskResponseDTO> findTaskByTitle(String title) {
        return taskRepository.findByTitleContainingIgnoreCase(title).stream()
                .findFirst()
                .map(TaskResponseMapper.INSTANCE::toDTO);
    }

    // Methods using JPQL queries

    public List<TaskResponseDTO> getTasksByPriority(int priorityValue) {
        return taskRepository.findTasksByPriorityValue(priorityValue).stream()
                .map(TaskResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    // Methods using native queries
    @Transactional
    public Map<String, Object> getTaskStatusStatistics() {
        List<Object[]> rawStats = taskRepository.getTaskStatusStatistics();

        Map<String, Object> formattedStats = new HashMap<>();
        formattedStats.put("totalTasks", taskRepository.count());

        List<Map<String, Object>> statusStats = rawStats.stream()
                .map(row -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("status", row[0]);
                    stat.put("count", row[1]);
                    stat.put("oldestTask", row[2]);
                    stat.put("newestTask", row[3]);
                    return stat;
                })
                .collect(Collectors.toList());

        formattedStats.put("statusBreakdown", statusStats);
        return formattedStats;
    }

    // Observer pattern methods
    public void addTaskCreationListener(Consumer<Task> listener) {
        taskCreationListeners.add(listener);
    }

    public void addTaskCompletionListener(Consumer<Task> listener) {
        taskCompletionListeners.add(listener);
    }

    public TaskStatistics getTaskStatistics() {
        long total = taskRepository.count();
        long pending = taskRepository.countByStatus(Task.TaskStatus.PENDING);
        long inProgress = taskRepository.countByStatus(Task.TaskStatus.IN_PROGRESS);
        long blocked = taskRepository.countByStatus(Task.TaskStatus.BLOCKED);
        long completed = taskRepository.countByStatus(Task.TaskStatus.COMPLETED);

        return new TaskStatistics(total, pending, inProgress, blocked, completed);
    }

    private long countTasksByStatus(Task.TaskStatus status, List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .count();
    }

    // Java 15: Text blocks for complex queries
    public String generateTaskReport() {
        List<Task> tasks = taskRepository.findAll();
        int taskSize = tasks.size();
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
                taskSize,
                countTasksByStatus(Task.TaskStatus.PENDING, tasks),
                countTasksByStatus(Task.TaskStatus.IN_PROGRESS, tasks),
                countTasksByStatus(Task.TaskStatus.BLOCKED, tasks),
                countTasksByStatus(Task.TaskStatus.COMPLETED, tasks),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    // Java 16: Pattern matching for instanceof
    public String getTaskDescription(Object taskIdentifier) {
        switch (taskIdentifier) {
            case UUID id -> {
                Task task = taskRepository.findById(id).get(); // check if task exists
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
                Task task = taskRepository.findById(id).get(); // check if task exists
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
        Task task = taskRepository.findById(id).get(); // check if task exists
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
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
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
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .collect(Collectors.groupingBy(
                        Task::getStatus,
                        Collectors.mapping(TaskResponseMapper.INSTANCE::toDTO, Collectors.toList())
                ));
    }

    // Use the TaskPriority sealed interface
    public Task.Priority getTaskPriorityObject(UUID id) {
        Task task = taskRepository.findById(id).get(); // check if task exists
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
    public Task updateTaskPriority(UUID id, Task.Priority priority) {
        Task task = taskRepository.findById(id).get(); // check if task exists
        if (task == null) {
            throw new IllegalArgumentException("Task not found with ID: " + id);
        }

        // We need to update the Task model to include priority
        // For now, just returning the task since we can't modify it
        // In a real implementation, this would return a new Task with updated priority
        return task;
    }

}

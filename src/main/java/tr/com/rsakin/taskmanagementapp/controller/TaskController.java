package tr.com.rsakin.taskmanagementapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.rsakin.taskmanagementapp.model.dto.request.PriorityUpdateRequest;
import tr.com.rsakin.taskmanagementapp.model.dto.request.StatusUpdateRequest;
import tr.com.rsakin.taskmanagementapp.model.dto.request.TaskRequest;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;
import tr.com.rsakin.taskmanagementapp.service.TaskService;
import tr.com.rsakin.taskmanagementapp.service.TaskStatusNotAvailableException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    // Composition over inheritance
    // - More Flexibility: Inheritance creates a tight coupling between the parent and child classes, whereas composition allows objects to be more modular.
    // - Easier Code Maintenance: With composition, you can change behaviors by swapping out components instead of modifying a whole class hierarchy.
    // - Avoids Deep Inheritance Trees: Inheritance can lead to complex, hard-to-maintain structures, while composition keeps relationships simpler.
    // - Encapsulation & Reusability:
    private final TaskService taskService;

    // Clean code: Constructor injection instead of field injection
    // Constructor Injection
    // Other Injection options: Setter Injection, Field Injection
    // Constructor Injection:
    // - It allows you to inject dependencies into the constructor of a class.
    // - It is useful for dependency injection in Spring, where you can inject dependencies into classes using constructor injection.
    // - It is also useful for testability, as it allows you to mock dependencies in unit tests.
    @Autowired
    // What is annotation : An annotation is a special type of metadata that you can attach to classes, methods, and other elements in a Java program.
    // - It is a Spring annotation that tells Spring to automatically inject the taskService field with the appropriate object instance.
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // HTTP Methods : GET, POST, PUT, PATCH, DELETE
    // HTTP Status Codes : 200, 201, 204, 400, 404, 500
    // HTTP Headers : Content-Type, Accept, Authorization
    // HTTP Body : JSON, XML
    // HTTP Response : JSON, XML
    // HyperText : HyperText is a term used to describe the structure and content of documents on the internet.
    // What is HTTP : HTTP is a protocol for transferring data over the internet.
    // What is REST : REST is an architectural style for building web services.
    // What is API : API is a set of endpoints that allow clients to interact with a server.
    // What is JSON : JSON is a data format that is used to transfer data over the internet.
    // What is XML : XML is a data format that is used to transfer data over the internet.
    // Endpoint : A URL that is used to access a resource on a server.
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    // HTTP Post
    // HTTP Status Code : 201
    // HTTP Body : JSON
    // HTTP Response : JSON

    // @RequestBody : It is a Spring annotation that tells Spring to automatically deserialize the request body into the TaskRequest object.
    public ResponseEntity<TaskResponseDTO> createTask(@RequestBody TaskRequest request) {
        TaskResponseDTO newTask = taskService.createTask(request.getTitle(), request.getDescription());
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }

    // CRUD Operations
    // Create, Read, Update, Delete
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getAllTasks());
    }

    // What is URL : A URL (Uniform Resource Locator) is a string that specifies the location of a resource on the internet.
    // What is URI : A URI (Uniform Resource Identifier) is a string that specifies the location of a resource on the internet.
    // URI is more general than URL.
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable UUID id) {
        TaskResponseDTO task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
            // @PathVariable : It is a Spring annotation that tells Spring to automatically extract the value of the id parameter from the URL.
            @PathVariable UUID id,
            @RequestBody StatusUpdateRequest request) {

        try {
            Task updatedTask = taskService.updateTaskStatus(id, request.getStatus());
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (TaskStatusNotAvailableException ex) {
            // custom buss logic
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskResponseDTO>> searchTasks(@RequestParam String term) {
        return ResponseEntity.ok(taskService.searchTasks(term));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<TaskResponseDTO> findTaskByTitle(@PathVariable String title) {
        return taskService.findTaskByTitle(title)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/created-before")
    public ResponseEntity<List<TaskResponseDTO>> getTasksCreatedBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        return ResponseEntity.ok(taskService.getTasksCreatedBefore(dateTime));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<Task.TaskStatus>> getAvailableStatuses() {
        return ResponseEntity.ok(taskService.getAvailableStatuses());
    }

    @GetMapping("/statistics")
    public ResponseEntity<TaskService.TaskStatistics> getTaskStatistics() {
        return ResponseEntity.ok(taskService.getTaskStatistics());
    }

    @GetMapping("/count-by-status")
    public ResponseEntity<Map<Task.TaskStatus, Long>> getTaskCountByStatus() {
        return ResponseEntity.ok(taskService.getTaskCountByStatus());
    }

    @GetMapping("/{id}/priority")
    public ResponseEntity<String> getTaskPriority(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(taskService.getTaskPriority(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/report")
    public ResponseEntity<String> generateTaskReport() {
        return ResponseEntity.ok(taskService.generateTaskReport());
    }

    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<List<TaskResponseDTO>>> getTasksAsync() {
        return taskService.getTasksAsync()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/group-by-status")
    public ResponseEntity<Map<Task.TaskStatus, List<TaskResponseDTO>>> groupTasksByStatus() {
        return ResponseEntity.ok(taskService.groupTasksByStatus());
    }

    @PostMapping("/analyze-durations")
    public ResponseEntity<List<String>> analyzeTaskDurations(@RequestBody List<TaskService.TaskDuration> durations) {
        return ResponseEntity.ok(taskService.analyzeTaskDurations(durations));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<String> getTaskSummary(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(taskService.getTaskSummary(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/has-status/{status}")
    public ResponseEntity<Boolean> hasTaskWithStatus(@PathVariable Task.TaskStatus status) {
        return ResponseEntity.ok(taskService.hasTaskWithStatus(status));
    }

    @GetMapping("/{id}/priority-object")
    public ResponseEntity<Map<String, Object>> getTaskPriorityObject(@PathVariable UUID id) {
        try {
            Task.Priority priority = taskService.getTaskPriorityObject(id);

            // Create a map to represent the priority since we can't directly serialize the interface
            Map<String, Object> response = Map.of(
                    "label", priority.getLabel(),
                    "value", priority.getValue(),
                    "type", priority.getClass().getSimpleName()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/priority/{value}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByPriority(@PathVariable int value) {
        if (value < 1 || value > 3) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(taskService.getTasksByPriority(value));
    }

    @PostMapping("/{id}/priority")
    public ResponseEntity<Task> updateTaskPriority(
            @PathVariable UUID id,
            @RequestBody PriorityUpdateRequest request) {
        try {
            // Convert string or int to TaskPriority object based on request
            Task.Priority priority = switch(request.value()) {
                case 1 -> new Task.LowPriority();
                case 2 -> new Task.MediumPriority();
                case 3 -> new Task.HighPriority();
                default -> throw new IllegalArgumentException("Invalid priority value");
            };

            Task updatedTask = taskService.updateTaskPriority(id, priority);
            return ResponseEntity.ok(updatedTask);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}

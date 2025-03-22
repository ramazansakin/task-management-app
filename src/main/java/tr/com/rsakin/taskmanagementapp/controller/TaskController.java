package tr.com.rsakin.taskmanagementapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.rsakin.taskmanagementapp.model.dto.request.StatusUpdateRequest;
import tr.com.rsakin.taskmanagementapp.model.dto.request.TaskRequest;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;
import tr.com.rsakin.taskmanagementapp.service.TaskService;

import java.util.List;
import java.util.UUID;

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
        return ResponseEntity.ok(taskService.getAllTasks());
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
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}

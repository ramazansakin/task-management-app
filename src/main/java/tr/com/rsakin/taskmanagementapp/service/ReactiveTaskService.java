package tr.com.rsakin.taskmanagementapp.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Reactive Programming Concepts:
// - Asynchronous: Enables handling multiple requests simultaneously without blocking the main thread.
// - Non-blocking: Doesn't wait for a response before continuing execution, improving efficiency.
// - Reactive Streams: A specification that allows handling data streams efficiently with built-in mechanisms for processing data reactively.
// - Backpressure: A flow control mechanism that prevents overwhelming consumers by regulating the rate of data emission.
@Service
public class ReactiveTaskService {
    // In-memory storage (will replace with reactive DB in later weeks)
    // HashMap vs LinkedHashMap:
    // HashMap is a non-thread-safe implementation, while LinkedHashMap is thread-safe and provides better performance for concurrent operations.
    // HashMap is faster for non-concurrent operations, while LinkedHashMap is faster for concurrent operations.
    // LinkedHashMap is more memory-efficient than HashMap, as it keeps the order of insertion.
    // We can use whether HashMap or LinkedHashMap depending on the requirements
    private final Map<UUID, Task> taskStore = new HashMap<>();

    public Mono<Task> createTask(String title, String description) {
        return Mono.fromCallable(() -> {
            // Clean code: Validation
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Task title cannot be empty");
            }

            if (description == null) {
                throw new IllegalArgumentException("Task description cannot be null");
            }

            Task task = Task.builder()
                    .title(title)
                    .description(description)
                    .build();

            taskStore.put(task.getId(), task);
            return task;
        });
    }

    public Flux<Task> getAllTasks() {
//        return Flux.fromIterable(taskStore.values());
        return Flux.fromIterable(taskStore.values())
                .delayElements(Duration.ofSeconds(1));
    }

    public Mono<Task> getTaskById(UUID id) {
        return Mono.justOrEmpty(taskStore.get(id));
    }

    public Mono<Task> updateTaskStatus(UUID id, Task.TaskStatus newStatus) {
        return Mono.justOrEmpty(taskStore.get(id))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Task not found with ID: " + id)))
                .map(task -> {
                    Task updatedTask = task.updateStatus(newStatus);
                    taskStore.put(id, updatedTask);
                    return updatedTask;
                });
    }

    public Mono<Void> deleteTask(UUID id) {
        return Mono.fromRunnable(() -> taskStore.remove(id));
    }

}

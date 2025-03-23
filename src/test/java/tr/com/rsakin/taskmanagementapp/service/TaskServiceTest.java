package tr.com.rsakin.taskmanagementapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tr.com.rsakin.taskmanagementapp.model.dto.request.TaskRequest;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;
import tr.com.rsakin.taskmanagementapp.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTask() {
        TaskRequest request = new TaskRequest("Test Task", "Description");
        Task task = Task.builder().id(UUID.randomUUID()).title("Test Task").description("Description").build();
        TaskResponseDTO responseDTO = new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getCreatedAt(), task.getPriority());

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO createdTask = taskService.createTask(request.getTitle(), request.getDescription());

        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.title());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldReturnAllTasks() {
        Task task = new Task(UUID.randomUUID(), "Task 1", "Description", Task.TaskStatus.PENDING, LocalDateTime.now(), LocalDateTime.now(), 1, "Low");
        List<Task> tasks = Collections.singletonList(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<TaskResponseDTO> taskResponseDTOList = taskService.getAllTasks();

        assertNotNull(taskResponseDTOList);
        assertEquals(1, taskResponseDTOList.size());
    }

    @Test
    void shouldReturnNullWhenTaskNotFoundById() {
        UUID nonExistingId = UUID.randomUUID();
        when(taskRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        TaskResponseDTO task = taskService.getTaskById(nonExistingId);

        assertNull(task);
    }

    @Test
    void shouldUpdateTaskStatus() {
        UUID taskId = UUID.randomUUID();
        Task task = new Task(taskId, "Task 1", "Description", Task.TaskStatus.PENDING, LocalDateTime.now(), LocalDateTime.now(), 1, "Low");
        Task updatedTask = task.updateStatus(Task.TaskStatus.COMPLETED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task savedTask = taskService.updateTaskStatus(taskId, Task.TaskStatus.COMPLETED);

        assertNotNull(savedTask);
        assertEquals(Task.TaskStatus.COMPLETED, savedTask.getStatus());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFoundForUpdate() {
        UUID nonExistingId = UUID.randomUUID();
        when(taskRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> taskService.updateTaskStatus(nonExistingId, Task.TaskStatus.COMPLETED));
    }

    @Test
    void shouldReturnTasksByStatus() {
        Task task = new Task(UUID.randomUUID(), "Task 1", "Description", Task.TaskStatus.PENDING, LocalDateTime.now(), LocalDateTime.now(), 1, "Low");
        List<Task> tasks = Collections.singletonList(task);
        when(taskRepository.findByStatus(Task.TaskStatus.PENDING)).thenReturn(tasks);

        List<TaskResponseDTO> taskResponseDTOList = taskService.getTasksByStatus(Task.TaskStatus.PENDING);

        assertNotNull(taskResponseDTOList);
        assertEquals(1, taskResponseDTOList.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoTasks() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        List<TaskResponseDTO> taskResponseDTOList = taskService.getAllTasks();

        assertTrue(taskResponseDTOList.isEmpty());
    }

}

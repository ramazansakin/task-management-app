package tr.com.rsakin.taskmanagementapp.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import tr.com.rsakin.taskmanagementapp.model.dto.request.StatusUpdateRequest;
import tr.com.rsakin.taskmanagementapp.model.dto.request.TaskRequest;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;
import tr.com.rsakin.taskmanagementapp.security.JwtUtil;
import tr.com.rsakin.taskmanagementapp.service.TaskService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private String token;

    @BeforeAll
    static void setUp() {

        // Token generation
        JwtUtil jwtUtil = new JwtUtil();
        // UserDetails userDetails = CustomUserDetailsService.loadUserByUsername("user");
        // token = jwtUtil.generateToken(userDetails);
        // add token to header
    }

    // 3A - Arrange, Act, Assert
    @Test
    void shouldCreateTask() {

        // Arrange
        TaskRequest request = new TaskRequest("Test Task", "Description");
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(UUID.randomUUID(), "Test Task",
                "Description", Task.TaskStatus.PENDING, LocalDateTime.now(), new Task.LowPriority());

        // Act
        when(taskService.createTask(any(String.class), anyString())).thenReturn(taskResponseDTO);

        ResponseEntity<TaskResponseDTO> response = taskController.createTask(request);

        System.out.println("Response : " + response);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // 1
        assertNotNull(response.getBody()); // 2
        assertEquals(taskResponseDTO.title(), response.getBody().title()); // 3
    }

    @Test
    void shouldGetAllTasks() {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(UUID.randomUUID(), "Test Task",
                "Description", Task.TaskStatus.PENDING, LocalDateTime.now(), new Task.LowPriority());
        List<TaskResponseDTO> taskResponseDTOList = Collections.singletonList(taskResponseDTO);

        when(taskService.getAllTasks()).thenReturn(taskResponseDTOList);

        ResponseEntity<List<TaskResponseDTO>> response = taskController.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void shouldGetTaskById() {
        UUID taskId = UUID.randomUUID();
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(taskId, "Test Task",
                "Description", Task.TaskStatus.PENDING, LocalDateTime.now(), new Task.LowPriority());

        when(taskService.getTaskById(taskId)).thenReturn(taskResponseDTO);

        ResponseEntity<TaskResponseDTO> response = taskController.getTaskById(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(taskId, response.getBody().id());
    }

    @Test
    void shouldReturnNotFoundWhenTaskByIdDoesNotExist() {
        UUID nonExistingId = UUID.randomUUID();

        when(taskService.getTaskById(any(UUID.class))).thenReturn(null);

        ResponseEntity<TaskResponseDTO> response = taskController.getTaskById(nonExistingId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldUpdateTaskStatus() {
        UUID taskId = UUID.randomUUID();
        StatusUpdateRequest request = new StatusUpdateRequest(Task.TaskStatus.COMPLETED);
        Task updatedTask = new Task(taskId, "Test Task", "Description", Task.TaskStatus.COMPLETED, LocalDateTime.now(), LocalDateTime.now(), 1, "Low");

        when(taskService.updateTaskStatus(taskId, Task.TaskStatus.COMPLETED)).thenReturn(updatedTask);

        ResponseEntity<Task> response = taskController.updateTaskStatus(taskId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Task.TaskStatus.COMPLETED, response.getBody().getStatus());
    }

    @Test
    void shouldReturnNotFoundWhenTaskForStatusUpdateDoesNotExist() {
        UUID nonExistingId = UUID.randomUUID();
        StatusUpdateRequest request = new StatusUpdateRequest(Task.TaskStatus.COMPLETED);

        when(taskService.updateTaskStatus(nonExistingId, Task.TaskStatus.COMPLETED)).thenThrow(IllegalArgumentException.class);

        ResponseEntity<Task> response = taskController.updateTaskStatus(nonExistingId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldDeleteTask() {
        // Arrange
        UUID taskId = UUID.randomUUID();

        // Act
        doNothing().when(taskService).deleteTask(taskId);

        ResponseEntity<Void> response = taskController.deleteTask(taskId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService).deleteTask(taskId);
    }

    @Test
    void shouldGetTasksByStatus() {
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(UUID.randomUUID(), "Test Task", "Description", Task.TaskStatus.PENDING, LocalDateTime.now(), new Task.LowPriority());
        List<TaskResponseDTO> taskResponseDTOList = Collections.singletonList(taskResponseDTO);

        when(taskService.getTasksByStatus(Task.TaskStatus.PENDING)).thenReturn(taskResponseDTOList);

        ResponseEntity<List<TaskResponseDTO>> response = taskController.getTasksByStatus(Task.TaskStatus.PENDING);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

}



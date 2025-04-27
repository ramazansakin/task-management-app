package tr.com.rsakin.taskmanagementapp.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tr.com.rsakin.taskmanagementapp.model.dto.request.TaskRequest;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;
import tr.com.rsakin.taskmanagementapp.model.exception.TaskNotFoundException;
import tr.com.rsakin.taskmanagementapp.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    // 3A - Arrange/Init, Act/Stub, Assert/Validation

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        // Clear all interactions before each test
    }

    @BeforeAll
    static void beforeAll() {
        // Do something before all tests
    }

    @AfterEach
    void tearDown() {
        // Clear all interactions after each test
    }

    @AfterAll
    static void afterAll() {
        // Do something after all tests
    }


    @Test
    // @ParameterizedTest(name = "{index} => title={0}, description={1}")
    void shouldCreateTask_successfull() {
        // Arrange - Initialize
        TaskRequest request = new TaskRequest("Test Task", "Description");
        Task task = Task.builder().id(UUID.randomUUID()).title("Test Task").description("Description").build();
        // Expected response
        TaskResponseDTO expectedResponse = new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getCreatedAt(), task.getPriority());

        // Act - Stubbing
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Actual response
        TaskResponseDTO actualResponse = taskService.createTask(request.getTitle(), request.getDescription());

        System.out.println("# Actual response : " + actualResponse);

        // Assert - Validation
        assertNotNull(actualResponse); // 1
        assertEquals(expectedResponse.title(), actualResponse.title()); // 2
        assertEquals(expectedResponse.description(), actualResponse.description()); // 3
        verify(taskRepository).save(any(Task.class)); // 4
    }

    @Test
    void shouldReturnAllTasks() {
        // Arrange - Initialize
        Task task = new Task(UUID.randomUUID(), "Task 1", "Description",
                Task.TaskStatus.PENDING, LocalDateTime.now(), LocalDateTime.now(), 1, "Low");
        Task task2 = new Task(UUID.randomUUID(), "Task 2", "Description 2",
                Task.TaskStatus.BLOCKED, LocalDateTime.now(), LocalDateTime.now(), 2, "Medium");

        List<Task> expectedTasks = new ArrayList<>();
        expectedTasks.addAll(Arrays.asList(task, task2));

        // Act - Stubbing
        when(taskRepository.findAll()).thenReturn(expectedTasks);

        // Actual response
        List<TaskResponseDTO> actualResponse = taskService.getAllTasks();

        // Assert - Validation
        assertNotNull(actualResponse);
        assertEquals(expectedTasks.size(), actualResponse.size());
    }

    @Test
    void shouldReturnNullWhenTaskNotFoundById_Failure() {
        // Arrange - Initialize
        UUID nonExistingId = UUID.randomUUID();

        // Act - Stubbing
        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Actual response
        TaskResponseDTO task = taskService.getTaskById(nonExistingId);

        // Assert - Validation
        assertNull(task);
    }

    @Test
    void shouldUpdateTaskStatus() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        Task task = new Task(taskId, "Task 1", "Description", Task.TaskStatus.PENDING, LocalDateTime.now(), LocalDateTime.now(), 1, "Low");
        Task updatedTask = task.updateStatus(Task.TaskStatus.COMPLETED);

        // Act
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        Task actualResponse = taskService.updateTaskStatus(taskId, Task.TaskStatus.COMPLETED);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(updatedTask.getStatus(), actualResponse.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // Edge Case
    @Test
    void shouldThrowExceptionWhenTaskNotFoundForUpdate() {
        UUID nonExistingId = UUID.randomUUID();

        when(taskRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskStatus(nonExistingId, Task.TaskStatus.COMPLETED));
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

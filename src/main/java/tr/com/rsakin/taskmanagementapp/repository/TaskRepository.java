package tr.com.rsakin.taskmanagementapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Standard JPA method queries
    // SELECT * FROM tasks WHERE status = "DONE"
    List<Task> findByStatus(Task.TaskStatus status);

    // "abc", "ABt", "aBy", "ghjgjh-ab-asdasd"
    // "ab"
    // WHERE title ILIKE "%ab%"
    List<Task> findByTitleContainingIgnoreCase(String title);

    Optional<Task> findFirstByOrderByCreatedAtDesc();

    long countByStatus(Task.TaskStatus status);

    List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // JPQL queries
    @Query("SELECT t FROM Task t WHERE t.priorityValue = :value ORDER BY t.createdAt DESC")
    List<Task> findTasksByPriorityValue(@Param("value") int priorityValue);

    @Query("SELECT t FROM Task t WHERE t.createdAt < :date AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("date") LocalDateTime date);

    // Native SQL queries
    @Query(value = """
            SELECT * FROM tasks
            WHERE status != 'COMPLETED'
            AND priority_value >= :minPriority
            ORDER BY priority_value DESC, created_at ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Task> findPriorityTasksToComplete(
            @Param("minPriority") int minPriority,
            @Param("limit") int limit);

    @Query(value = """
            SELECT 
                status,
                COUNT(*) as task_count,
                MIN(created_at) as oldest_task,
                MAX(created_at) as newest_task
            FROM tasks
            GROUP BY status
            """, nativeQuery = true)
    List<Object[]> getTaskStatusStatistics();

}

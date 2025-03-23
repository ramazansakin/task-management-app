package tr.com.rsakin.taskmanagementapp.model.dto.request;

import tr.com.rsakin.taskmanagementapp.model.entity.Task;

public record StatusUpdateRequest(Task.TaskStatus status) {
}

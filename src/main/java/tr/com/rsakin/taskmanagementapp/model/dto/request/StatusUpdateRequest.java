package tr.com.rsakin.taskmanagementapp.model.dto.request;

import tr.com.rsakin.taskmanagementapp.model.entity.Task;

public class StatusUpdateRequest {
    private Task.TaskStatus status;

    public Task.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(Task.TaskStatus status) {
        this.status = status;
    }
}

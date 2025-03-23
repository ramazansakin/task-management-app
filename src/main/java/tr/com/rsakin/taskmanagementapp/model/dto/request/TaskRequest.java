package tr.com.rsakin.taskmanagementapp.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO : Data Transfer Object
// POJO : Plain Old Java Object
// DTO vs POJO :
// DTO is a data transfer object that is used to transfer data between the client and the server.
// POJO is a plain old java object that is used to store data in memory.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private String title;
    private String description;
}

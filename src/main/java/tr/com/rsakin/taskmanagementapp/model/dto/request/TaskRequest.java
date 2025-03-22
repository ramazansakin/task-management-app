package tr.com.rsakin.taskmanagementapp.model.dto.request;

// DTO : Data Transfer Object
// POJO : Plain Old Java Object
// DTO vs POJO :
// DTO is a data transfer object that is used to transfer data between the client and the server.
// POJO is a plain old java object that is used to store data in memory.
public class TaskRequest {

    private String title;
    private String description;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

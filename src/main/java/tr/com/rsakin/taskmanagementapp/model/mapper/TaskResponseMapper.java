package tr.com.rsakin.taskmanagementapp.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tr.com.rsakin.taskmanagementapp.model.dto.response.TaskResponseDTO;
import tr.com.rsakin.taskmanagementapp.model.entity.Task;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskResponseMapper {

    TaskResponseMapper INSTANCE = Mappers.getMapper(TaskResponseMapper.class);

    TaskResponseDTO toDTO(Task task);

    List<TaskResponseDTO> toDTOList(List<Task> tasks);
}


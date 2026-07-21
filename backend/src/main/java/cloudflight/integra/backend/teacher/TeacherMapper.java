package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import cloudflight.integra.backend.teacher.model.TeacherDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    TeacherDto toDto(Teacher teacher);

    Teacher toEntity(TeacherDto dto);
}

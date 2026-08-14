package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import cloudflight.integra.backend.teacher.model.TeacherDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    TeacherDto toDto(Teacher teacher);

    @Mapping(target = "passwordHash", ignore = true)
    Teacher toEntity(TeacherDto dto);
}

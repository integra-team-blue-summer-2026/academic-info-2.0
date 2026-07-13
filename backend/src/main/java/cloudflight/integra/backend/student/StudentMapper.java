package cloudflight.integra.backend.student;

import cloudflight.integra.backend.student.model.Student;
import cloudflight.integra.backend.student.model.StudentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentDto toDto(Student student);
    Student toEntity(StudentDto dto);
}

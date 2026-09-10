package cloudflight.integra.backend.studentcourse;

import cloudflight.integra.backend.studentcourse.model.StudentCourse;
import cloudflight.integra.backend.studentcourse.model.StudentCourseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentCourseMapper {
    StudentCourseDto toDto(StudentCourse studentCourse);

    StudentCourse toEntity(StudentCourseDto studentCourseDto);
}

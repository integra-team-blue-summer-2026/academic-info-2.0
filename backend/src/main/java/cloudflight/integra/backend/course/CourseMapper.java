package cloudflight.integra.backend.course;

import cloudflight.integra.backend.course.model.Course;
import cloudflight.integra.backend.course.model.CourseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDto toDto(Course course);

    Course toEntity(CourseDto dto);
}

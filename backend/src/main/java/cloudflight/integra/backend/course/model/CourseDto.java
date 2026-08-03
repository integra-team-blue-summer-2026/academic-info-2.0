package cloudflight.integra.backend.course.model;

import java.util.UUID;

public record CourseDto(
    UUID id,
    UUID teacherId,
    String courseName,
    String syllabus,
    int credits,
    String description
) {
}

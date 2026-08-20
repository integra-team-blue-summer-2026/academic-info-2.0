package cloudflight.integra.backend.studentcourse.model;

import java.util.UUID;

public record StudentCourseDto(
    UUID id,
    UUID studentId,
    UUID courseId
) {
}

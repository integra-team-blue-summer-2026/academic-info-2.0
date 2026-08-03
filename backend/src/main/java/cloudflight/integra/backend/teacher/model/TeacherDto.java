package cloudflight.integra.backend.teacher.model;

import java.util.UUID;

public record TeacherDto(
    UUID id,
    String firstName,
    String lastName,
    String title,
    String department
) {
}

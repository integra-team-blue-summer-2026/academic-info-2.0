package cloudflight.integra.backend.enrollment.model;

import java.util.UUID;

public record EnrollmentDto(
    UUID id,
    UUID studentId,
    UUID courseId,
    String studentName,
    String group,
    Integer semester,
    String academicYear
) {}

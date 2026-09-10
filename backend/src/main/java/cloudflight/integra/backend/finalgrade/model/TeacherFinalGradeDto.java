package cloudflight.integra.backend.finalgrade.model;

import java.util.UUID;

public record TeacherFinalGradeDto(
    UUID id,
    UUID courseId,
    UUID studentId,
    String courseName,
    String studentName,
    int semester,
    int credits,
    Boolean provisional,
    int grade,
    String completionDate
) {}

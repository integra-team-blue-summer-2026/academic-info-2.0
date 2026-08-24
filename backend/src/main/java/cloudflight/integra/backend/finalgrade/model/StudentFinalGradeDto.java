package cloudflight.integra.backend.finalgrade.model;

import java.util.UUID;

public record StudentFinalGradeDto(
    UUID id,
    UUID studentId,
    UUID courseId,
    String courseName,
    int semester,
    int credits,
    Boolean provisional,
    int grade,
    String completionDate
) {}

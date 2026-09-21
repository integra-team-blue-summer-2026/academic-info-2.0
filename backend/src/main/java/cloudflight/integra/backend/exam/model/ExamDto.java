package cloudflight.integra.backend.exam.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExamDto (
    UUID id,
    UUID courseId,
    UUID teacherId,
    ExamType examType,
    ExamFormat examFormat,
    String group,
    int duration,
    LocalDateTime primaryDate,
    LocalDateTime secondaryDate,
    String room) {}

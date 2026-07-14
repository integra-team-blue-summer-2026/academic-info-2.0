package cloudflight.integra.backend.exam.model;

import java.util.UUID;

public record ExamDto (UUID id, UUID courseId, String examType, String examDate, String room) {
}

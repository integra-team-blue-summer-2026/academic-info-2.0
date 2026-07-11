package cloudflight.integra.backend.exam.model;

public record ExamDto (Long id, Long courseId, String examType, String examDate, String room) {
}

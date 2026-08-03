package cloudflight.integra.backend.studentexam.model;

import java.util.UUID;

public record StudentExamDto(UUID id, UUID studentId, UUID examId, String sessionGrade, String resitGrade, GradeStatus gradeStatus) {
}

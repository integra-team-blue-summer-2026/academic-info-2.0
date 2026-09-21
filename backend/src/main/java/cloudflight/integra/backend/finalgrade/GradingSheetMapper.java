package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.enrollment.model.Enrollment;
import cloudflight.integra.backend.finalgrade.model.FinalGrade;
import cloudflight.integra.backend.finalgrade.model.StudentGradeRowDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class GradingSheetMapper {

    public StudentGradeRowDto toDto(Enrollment enrollment, FinalGrade grade, String group, String studentName) {
        StudentGradeRowDto dto = new StudentGradeRowDto();

        dto.setStudentId(enrollment.getStudentId());
        dto.setGroup(group);
        dto.setStudentName(studentName);

        if (grade != null) {
            dto.setFinalGradeId(grade.getId());
            dto.setGrade(grade.getGrade());
            dto.setProvisional(grade.getProvisional());
            dto.setCompletionDate(grade.getCompletionDate() != null ? grade.getCompletionDate().toString() : null);
        }

        return dto;
    }
}

package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.finalgrade.model.FinalGrade;
import cloudflight.integra.backend.finalgrade.model.TeacherFinalGradeDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeacherFinalGradeMapper {
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.courseName", target = "courseName")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(target = "studentName",
        expression = "java(finalGrade.getStudent() != null ? finalGrade.getStudent().getLastName() + \" \" + finalGrade.getStudent().getFirstName() : \"\")"
    )
    @Mapping(source = "course.credits", target = "credits")
    TeacherFinalGradeDto toDto(FinalGrade finalGrade);

    @InheritInverseConfiguration
    FinalGrade toEntity(TeacherFinalGradeDto dto);
}

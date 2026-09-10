package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.finalgrade.model.FinalGrade;
import cloudflight.integra.backend.finalgrade.model.StudentFinalGradeDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentFinalGradeMapper {

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.courseName", target = "courseName")
    @Mapping(source = "course.credits", target = "credits")
    @Mapping(source = "student.id", target = "studentId")
    StudentFinalGradeDto toDto(FinalGrade finalGrade);

    @InheritInverseConfiguration
    FinalGrade toEntity(StudentFinalGradeDto dto);
}

package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.course.model.Course;
import cloudflight.integra.backend.finalgrade.model.FinalGrade;
import cloudflight.integra.backend.finalgrade.model.StudentFinalGradeDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentFinalGradeMapper {

    @Mapping(source = "finalGrade.id", target = "id")
    @Mapping(source = "finalGrade.semester", target = "semester")
    @Mapping(source = "finalGrade.grade", target = "grade")
    @Mapping(source = "finalGrade.provisional", target = "provisional")
    @Mapping(source = "finalGrade.completionDate", target = "completionDate")
    @Mapping(source = "finalGrade.studentId", target = "studentId")
    @Mapping(source = "finalGrade.courseId", target = "courseId")
    @Mapping(source = "course.courseName", target = "courseName")
    @Mapping(source = "course.credits", target = "credits")
    StudentFinalGradeDto toDto(FinalGrade finalGrade, Course course);

    FinalGrade toEntity(StudentFinalGradeDto dto);
}

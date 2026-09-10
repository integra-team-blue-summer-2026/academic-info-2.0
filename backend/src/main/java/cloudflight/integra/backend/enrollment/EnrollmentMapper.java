package cloudflight.integra.backend.enrollment;

import cloudflight.integra.backend.enrollment.model.Enrollment;
import cloudflight.integra.backend.enrollment.model.EnrollmentDto;
import cloudflight.integra.backend.student.model.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "id", source = "enrollment.id")
    @Mapping(target = "courseId", source = "enrollment.courseId")
    @Mapping(target = "studentId", source = "enrollment.studentId")
    @Mapping(target = "semester", source = "enrollment.semester")
    @Mapping(target = "academicYear", source = "enrollment.academicYear")
    @Mapping(target = "group", expression = "java(student.getGroup())")
    @Mapping(
        target = "studentName",
        expression = "java(student != null ? student.getLastName() + \" \" + student.getFirstName() : \"Unknown Student\")"
    )
    EnrollmentDto toDto(Enrollment enrollment, Student student);

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "courseId", source = "dto.courseId")
    @Mapping(target = "studentId", source = "dto.studentId")
    @Mapping(target = "semester", source = "dto.semester")
    @Mapping(target = "academicYear", source = "dto.academicYear")
    Enrollment toEntity(EnrollmentDto dto);
}

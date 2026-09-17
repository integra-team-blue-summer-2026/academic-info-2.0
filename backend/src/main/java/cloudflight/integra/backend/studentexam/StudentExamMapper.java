package cloudflight.integra.backend.studentexam;

import cloudflight.integra.backend.student.model.Student;
import cloudflight.integra.backend.studentexam.model.StudentExam;
import cloudflight.integra.backend.studentexam.model.StudentExamDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentExamMapper {

    @Mapping(target = "id", source = "studentExam.id")
    @Mapping(target = "studentId", source = "studentExam.studentId")
    @Mapping(target = "examId", source = "studentExam.examId")
    @Mapping(target = "sessionGrade", source = "studentExam.sessionGrade")
    @Mapping(target = "resitGrade", source = "studentExam.resitGrade")
    @Mapping(target = "gradeStatus", source = "studentExam.gradeStatus")
    @Mapping(target = "studentName", expression = "java(student != null ? student.getFirstName() + \" \" + student.getLastName() : \"Unknown\")")
    @Mapping(target = "group", expression = "java(student != null ? student.getGroup() : \"-\")")
    StudentExamDto toDto(StudentExam studentExam, Student student);

    @Mapping(target = "studentName", ignore = true)
    @Mapping(target = "group", ignore = true)
    StudentExamDto toDto(StudentExam studentExam);

    StudentExam toEntity(StudentExamDto studentExamDto);
}

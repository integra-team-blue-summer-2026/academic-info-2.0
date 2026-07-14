package cloudflight.integra.backend.studentexam;

import cloudflight.integra.backend.studentexam.model.StudentExam;
import cloudflight.integra.backend.studentexam.model.StudentExamDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentExamMapper {
    StudentExamDto toDto(StudentExam studentExam);

    StudentExam toEntity(StudentExamDto studentExamDto);
}

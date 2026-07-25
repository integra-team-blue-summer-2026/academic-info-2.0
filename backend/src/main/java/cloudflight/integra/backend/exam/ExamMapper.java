package cloudflight.integra.backend.exam;

import cloudflight.integra.backend.exam.model.Exam;
import cloudflight.integra.backend.exam.model.ExamDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExamMapper {
    ExamDto toDto(Exam exam);

    Exam toEntity(ExamDto dto);
}

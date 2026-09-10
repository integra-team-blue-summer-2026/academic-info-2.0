package cloudflight.integra.backend.examregistration;

import cloudflight.integra.backend.examregistration.model.ExamRegistration;
import cloudflight.integra.backend.examregistration.model.ExamRegistrationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExamRegistrationMapper {
    ExamRegistrationDto toDto(ExamRegistration registration);
    ExamRegistration toEntity(ExamRegistrationDto dto);
}

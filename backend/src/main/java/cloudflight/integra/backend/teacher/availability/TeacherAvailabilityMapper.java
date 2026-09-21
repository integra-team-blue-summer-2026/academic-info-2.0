package cloudflight.integra.backend.teacher.availability;

import cloudflight.integra.backend.teacher.availability.model.TeacherAvailability;
import cloudflight.integra.backend.teacher.availability.model.TeacherAvailabilityDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeacherAvailabilityMapper {

    TeacherAvailabilityDto toDto(TeacherAvailability availability);

    TeacherAvailability toEntity(TeacherAvailabilityDto dto);
}

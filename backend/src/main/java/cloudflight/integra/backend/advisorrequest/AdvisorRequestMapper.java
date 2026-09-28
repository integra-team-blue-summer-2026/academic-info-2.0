package cloudflight.integra.backend.advisorrequest;

import cloudflight.integra.backend.advisorrequest.model.AdvisorRequest;
import cloudflight.integra.backend.advisorrequest.model.AdvisorRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdvisorRequestMapper {
    AdvisorRequestDto toDto(AdvisorRequest request);
    AdvisorRequest toEntity(AdvisorRequestDto dto);

}

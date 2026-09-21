package cloudflight.integra.backend.thesis;

import cloudflight.integra.backend.thesis.model.Thesis;
import cloudflight.integra.backend.thesis.model.ThesisDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ThesisMapper {

    ThesisDto toDto(Thesis thesis);
}

package cloudflight.integra.backend.graduationthesis;

import cloudflight.integra.backend.graduationthesis.model.GraduationThesis;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GraduationThesisMapper {

    GraduationThesisDto toDto(GraduationThesis graduationThesis);

    GraduationThesis toEntity(GraduationThesisDto graduationThesisDto);
}

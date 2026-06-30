package cloudflight.integra.backend.coffee;

import cloudflight.integra.backend.coffee.model.Coffee;
import cloudflight.integra.backend.coffee.model.CoffeeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoffeeMapper {
    CoffeeDto toDto(Coffee coffee);

    Coffee toEntity(CoffeeDto dto);
}

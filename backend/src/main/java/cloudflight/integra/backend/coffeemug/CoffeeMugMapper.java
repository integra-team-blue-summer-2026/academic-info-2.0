package cloudflight.integra.backend.coffeemug;

import cloudflight.integra.backend.coffee.CoffeeMapper;
import cloudflight.integra.backend.coffeemug.model.CoffeeMug;
import cloudflight.integra.backend.coffeemug.model.CoffeeMugDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CoffeeMapper.class)
public interface CoffeeMugMapper {
    CoffeeMugDto toDto(CoffeeMug mug);

    // TODO: add @Mapping(target = "coffee", ignore = true) when JPA is introduced
    CoffeeMug toEntity(CoffeeMugDto dto);
}

package cloudflight.integra.backend.room;

import cloudflight.integra.backend.room.model.Room;
import cloudflight.integra.backend.room.model.RoomAvailability;
import cloudflight.integra.backend.room.model.RoomAvailabilityDto;
import cloudflight.integra.backend.room.model.RoomDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomDto toDto(Room room);

    Room toEntity(RoomDto dto);

    RoomAvailabilityDto toDto(RoomAvailability availability);

    RoomAvailability toEntity(RoomAvailabilityDto dto);
}

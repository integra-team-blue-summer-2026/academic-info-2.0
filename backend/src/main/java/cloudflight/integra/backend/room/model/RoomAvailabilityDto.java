package cloudflight.integra.backend.room.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record RoomAvailabilityDto(
    UUID id,
    UUID roomId,
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime
) {
}

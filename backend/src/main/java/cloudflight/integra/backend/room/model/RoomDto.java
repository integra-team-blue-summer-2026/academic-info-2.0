package cloudflight.integra.backend.room.model;

import java.util.UUID;

public record RoomDto(
    UUID id,
    String roomName,
    String zone
) {
}

package cloudflight.integra.backend.room;

import cloudflight.integra.backend.room.model.Room;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomRepository {

    private final Map<UUID, Room> rooms = new HashMap<>();

    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }

    public Optional<Room> findById(UUID id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public Optional<Room> findByRoomName(String roomName) {
        return rooms.values().stream()
            .filter(room ->
                room.getRoomName() != null
                    && room.getRoomName().trim()
                    .equalsIgnoreCase(roomName.trim())
            )
            .findFirst();
    }

    public Room save(Room room) {
        if (room.getId() == null) {
            room.setId(UUID.randomUUID());
        }

        rooms.put(room.getId(), room);
        return room;
    }

    public void deleteById(UUID id) {
        rooms.remove(id);
    }
}

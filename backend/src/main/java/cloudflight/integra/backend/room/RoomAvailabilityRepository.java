package cloudflight.integra.backend.room;

import cloudflight.integra.backend.room.model.RoomAvailability;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomAvailabilityRepository {

    private final Map<UUID, RoomAvailability> availabilities = new HashMap<>();

    public List<RoomAvailability> findAll() {
        return new ArrayList<>(availabilities.values());
    }

    public List<RoomAvailability> findByRoomId(UUID roomId) {
        return availabilities.values().stream()
            .filter(availability -> roomId.equals(availability.getRoomId()))
            .toList();
    }

    public Optional<RoomAvailability> findById(UUID id) {
        return Optional.ofNullable(availabilities.get(id));
    }

    public RoomAvailability save(RoomAvailability availability) {
        if (availability.getId() == null) {
            availability.setId(UUID.randomUUID());
        }

        availabilities.put(availability.getId(), availability);
        return availability;
    }

    public void deleteById(UUID id) {
        availabilities.remove(id);
    }

    public void deleteByRoomId(UUID roomId) {
        availabilities.entrySet()
            .removeIf(entry ->
                roomId.equals(entry.getValue().getRoomId())
            );
    }
}

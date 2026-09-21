package cloudflight.integra.backend.room;

import cloudflight.integra.backend.room.model.Room;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomRepository repository;
    private final RoomAvailabilityRepository availabilityRepository;

    public RoomService(
        RoomRepository repository,
        RoomAvailabilityRepository availabilityRepository
    ) {
        this.repository = repository;
        this.availabilityRepository = availabilityRepository;
    }

    public List<Room> getAll() {
        return repository.findAll();
    }

    public Optional<Room> getById(UUID id) {
        return repository.findById(id);
    }

    public Room create(Room room) {
        validateRoomName(room, null);

        room.setRoomName(room.getRoomName().trim());
        room.setZone(room.getZone().trim());

        return repository.save(room);
    }

    public Optional<Room> update(UUID id, Room room) {
        validateRoomName(room, id);

        return repository.findById(id).map(existing -> {
            room.setId(id);
            room.setRoomName(room.getRoomName().trim());
            room.setZone(room.getZone().trim());

            return repository.save(room);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id)
            .map(existing -> {
                availabilityRepository.deleteByRoomId(id);
                repository.deleteById(id);
                return true;
            })
            .orElse(false);
    }

    private void validateRoomName(Room room, UUID ignoredRoomId) {
        if (room.getRoomName() == null
            || room.getRoomName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Room name is required."
            );
        }

        if (room.getZone() == null
            || room.getZone().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Room zone is required."
            );
        }

        repository.findByRoomName(room.getRoomName())
            .filter(existing ->
                ignoredRoomId == null
                    || !ignoredRoomId.equals(existing.getId())
            )
            .ifPresent(existing -> {
                throw new IllegalArgumentException(
                    "A room with this name already exists."
                );
            });
    }
}

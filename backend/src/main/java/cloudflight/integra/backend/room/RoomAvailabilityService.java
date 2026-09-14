package cloudflight.integra.backend.room;

import cloudflight.integra.backend.room.model.RoomAvailability;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomAvailabilityService {

    private final RoomAvailabilityRepository repository;
    private final RoomRepository roomRepository;

    public RoomAvailabilityService(
        RoomAvailabilityRepository repository,
        RoomRepository roomRepository
    ) {
        this.repository = repository;
        this.roomRepository = roomRepository;
    }

    public List<RoomAvailability> getAll() {
        return repository.findAll();
    }

    public List<RoomAvailability> getByRoomId(UUID roomId) {
        return repository.findByRoomId(roomId);
    }

    public Optional<RoomAvailability> getById(UUID id) {
        return repository.findById(id);
    }

    public RoomAvailability create(RoomAvailability availability) {
        validateAvailability(availability, null);
        return repository.save(availability);
    }

    public Optional<RoomAvailability> update(
        UUID id,
        RoomAvailability availability
    ) {
        validateAvailability(availability, id);

        return repository.findById(id).map(existing -> {
            availability.setId(id);
            return repository.save(availability);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id)
            .map(existing -> {
                repository.deleteById(id);
                return true;
            })
            .orElse(false);
    }

    private void validateAvailability(
        RoomAvailability availability,
        UUID ignoredAvailabilityId
    ) {
        if (availability.getRoomId() == null) {
            throw new IllegalArgumentException(
                "Room is required."
            );
        }

        if (roomRepository.findById(availability.getRoomId()).isEmpty()) {
            throw new IllegalArgumentException(
                "Room does not exist."
            );
        }

        if (availability.getDayOfWeek() == null) {
            throw new IllegalArgumentException(
                "Day of week is required."
            );
        }

        if (availability.getDayOfWeek() == DayOfWeek.SATURDAY
            || availability.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException(
                "Room availability can only be set from Monday to Friday."
            );
        }

        if (availability.getStartTime() == null
            || availability.getEndTime() == null) {
            throw new IllegalArgumentException(
                "Start time and end time are required."
            );
        }

        if (!availability.getStartTime().isBefore(
            availability.getEndTime()
        )) {
            throw new IllegalArgumentException(
                "Start time must be earlier than end time."
            );
        }

        validateNoOverlap(
            availability,
            ignoredAvailabilityId
        );
    }

    private void validateNoOverlap(
        RoomAvailability availability,
        UUID ignoredAvailabilityId
    ) {
        List<RoomAvailability> existingAvailabilities =
            repository.findByRoomId(availability.getRoomId());

        boolean overlaps = existingAvailabilities.stream()
            .filter(existing ->
                ignoredAvailabilityId == null
                    || !ignoredAvailabilityId.equals(existing.getId())
            )
            .filter(existing ->
                existing.getDayOfWeek() == availability.getDayOfWeek()
            )
            .anyMatch(existing ->
                availability.getStartTime().isBefore(existing.getEndTime())
                    && availability.getEndTime().isAfter(existing.getStartTime())
            );

        if (overlaps) {
            throw new IllegalArgumentException(
                "The availability interval overlaps with an existing interval."
            );
        }
    }
}

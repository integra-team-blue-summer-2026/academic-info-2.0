package cloudflight.integra.backend.teacher.availability;

import cloudflight.integra.backend.teacher.availability.model.TeacherAvailability;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TeacherAvailabilityRepository {

    private final Map<UUID, TeacherAvailability> availability = new LinkedHashMap<>();

    public List<TeacherAvailability> findAll() {
        return new ArrayList<>(availability.values());
    }

    public Optional<TeacherAvailability> findById(UUID id) {
        return Optional.ofNullable(availability.get(id));
    }

    public List<TeacherAvailability> findByTeacherId(UUID teacherId) {
        return availability.values()
            .stream()
            .filter(item -> teacherId.equals(item.getTeacherId()))
            .toList();
    }

    public TeacherAvailability save(TeacherAvailability item) {
        if (item.getId() == null) {
            item.setId(UUID.randomUUID());
        }

        availability.put(item.getId(), item);
        return item;
    }

    public boolean deleteById(UUID id) {
        return availability.remove(id) != null;
    }

    public void deleteByTeacherId(UUID teacherId) {
        availability.entrySet().removeIf(
            entry -> teacherId.equals(entry.getValue().getTeacherId())
        );
    }
}

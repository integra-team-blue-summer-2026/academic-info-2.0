package cloudflight.integra.backend.teacher.availability;

import cloudflight.integra.backend.teacher.TeacherRepository;
import cloudflight.integra.backend.teacher.availability.model.TeacherAvailability;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeacherAvailabilityService {

    private final TeacherAvailabilityRepository repository;
    private final TeacherRepository teacherRepository;

    public TeacherAvailabilityService(
        TeacherAvailabilityRepository repository,
        TeacherRepository teacherRepository
    ) {
        this.repository = repository;
        this.teacherRepository = teacherRepository;
    }

    public List<TeacherAvailability> getByTeacherId(UUID teacherId) {
        ensureTeacherExists(teacherId);

        return repository.findByTeacherId(teacherId)
            .stream()
            .sorted(
                Comparator
                    .comparing(TeacherAvailability::getDayOfWeek)
                    .thenComparing(TeacherAvailability::getStartTime)
            )
            .toList();
    }

    public Optional<TeacherAvailability> getById(UUID id) {
        return repository.findById(id);
    }

    public TeacherAvailability create(TeacherAvailability availability) {
        validateAvailability(availability, null);

        return repository.save(availability);
    }

    public Optional<TeacherAvailability> update(
        UUID id,
        TeacherAvailability availability
    ) {
        return repository.findById(id)
            .map(existing -> {
                availability.setId(id);
                validateAvailability(availability, id);
                return repository.save(availability);
            });
    }

    public boolean delete(UUID id) {
        return repository.deleteById(id);
    }

    private void validateAvailability(
        TeacherAvailability availability,
        UUID availabilityIdToIgnore
    ) {
        if (availability == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Availability must not be null"
            );
        }

        if (availability.getTeacherId() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Teacher ID is required"
            );
        }

        ensureTeacherExists(availability.getTeacherId());

        if (availability.getDayOfWeek() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Day of week is required"
            );
        }

        if (availability.getDayOfWeek() == DayOfWeek.SATURDAY
            || availability.getDayOfWeek() == DayOfWeek.SUNDAY) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Teacher availability can only be set from Monday to Friday"
            );
        }

        if (availability.getStartTime() == null
            || availability.getEndTime() == null) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Start time and end time are required"
            );
        }

        if (!availability.getStartTime().isBefore(availability.getEndTime())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Start time must be before end time"
            );
        }

        validateNoOverlap(availability, availabilityIdToIgnore);
    }

    private void validateNoOverlap(
        TeacherAvailability availability,
        UUID availabilityIdToIgnore
    ) {
        List<TeacherAvailability> existing =
            repository.findByTeacherId(availability.getTeacherId());

        LocalTime newStart = availability.getStartTime();
        LocalTime newEnd = availability.getEndTime();

        boolean overlaps = existing.stream()
            .filter(existingItem ->
                availabilityIdToIgnore == null
                    || !existingItem.getId().equals(availabilityIdToIgnore)
            )
            .filter(existingItem ->
                existingItem.getDayOfWeek() == availability.getDayOfWeek()
            )
            .anyMatch(existingItem ->
                newStart.isBefore(existingItem.getEndTime())
                    && newEnd.isAfter(existingItem.getStartTime())
            );

        if (overlaps) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "The availability interval overlaps another interval on the same day"
            );
        }
    }

    private void ensureTeacherExists(UUID teacherId) {
        if (teacherRepository.findById(teacherId).isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Teacher not found"
            );
        }
    }
}

package cloudflight.integra.backend.teacher.availability.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record TeacherAvailabilityDto(
    UUID id,
    UUID teacherId,
    DayOfWeek dayOfWeek,
    LocalTime startTime,
    LocalTime endTime
) {
}

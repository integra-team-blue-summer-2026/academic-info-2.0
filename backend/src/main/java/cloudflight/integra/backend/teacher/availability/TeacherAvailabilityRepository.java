package cloudflight.integra.backend.teacher.availability;

import cloudflight.integra.backend.teacher.availability.model.TeacherAvailability;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface TeacherAvailabilityRepository extends JpaRepository<TeacherAvailability, UUID> {
    List<TeacherAvailability> findByTeacherId(UUID teacherId);
    @Transactional
    void deleteByTeacherId(UUID teacherId);
}

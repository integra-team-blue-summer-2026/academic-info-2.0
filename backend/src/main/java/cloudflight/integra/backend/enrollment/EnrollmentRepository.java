package cloudflight.integra.backend.enrollment;

import cloudflight.integra.backend.enrollment.model.Enrollment;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    List<Enrollment> getByCourseId(UUID courseId);
}

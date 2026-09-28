package cloudflight.integra.backend.examregistration;

import cloudflight.integra.backend.examregistration.model.ExamRegistration;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface ExamRegistrationRepository extends JpaRepository<ExamRegistration, UUID> {
    List<ExamRegistration> findByStudentId(UUID studentId);
    Optional<ExamRegistration> findByStudentIdAndExamId(UUID studentId, UUID examId);
}

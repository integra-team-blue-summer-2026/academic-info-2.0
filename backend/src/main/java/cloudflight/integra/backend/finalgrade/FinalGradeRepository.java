package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.finalgrade.model.FinalGrade;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface FinalGradeRepository extends JpaRepository<FinalGrade, UUID> {
    List<FinalGrade> findByStudentId(UUID studentId);
    List<FinalGrade> findByCourseId(UUID courseId);
    Optional<FinalGrade> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
}

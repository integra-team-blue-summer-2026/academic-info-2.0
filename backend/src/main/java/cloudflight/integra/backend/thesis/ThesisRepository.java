package cloudflight.integra.backend.thesis;

import cloudflight.integra.backend.thesis.model.Thesis;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface ThesisRepository extends JpaRepository<Thesis, UUID> {
    Optional<Thesis> findByStudentId(UUID studentId);
}

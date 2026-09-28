package cloudflight.integra.backend.exam;

import cloudflight.integra.backend.exam.model.Exam;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findByGroup(String group);
    List<Exam> findByRoom(String room);
}

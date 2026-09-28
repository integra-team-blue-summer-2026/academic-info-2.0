package cloudflight.integra.backend.studentexam;

import cloudflight.integra.backend.studentexam.model.StudentExam;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, UUID> {
}

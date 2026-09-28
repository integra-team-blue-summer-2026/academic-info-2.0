package cloudflight.integra.backend.student;

import cloudflight.integra.backend.student.model.Student;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
}

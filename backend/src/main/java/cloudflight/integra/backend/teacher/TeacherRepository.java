package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
}

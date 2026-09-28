package cloudflight.integra.backend.course;

import cloudflight.integra.backend.course.model.Course;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
}

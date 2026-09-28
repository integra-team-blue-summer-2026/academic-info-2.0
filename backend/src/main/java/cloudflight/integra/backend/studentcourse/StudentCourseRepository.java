package cloudflight.integra.backend.studentcourse;

import cloudflight.integra.backend.studentcourse.model.StudentCourse;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, UUID> {
    List<StudentCourse> findByCourseId(UUID courseId);
    Optional<StudentCourse> findByCourseIdAndStudentId(UUID courseId, UUID studentId);
    @Transactional
    long deleteByCourseIdAndStudentId(UUID courseId, UUID studentId);
}

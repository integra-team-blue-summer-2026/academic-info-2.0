package cloudflight.integra.backend.enrollment;

import cloudflight.integra.backend.enrollment.model.Enrollment;
import cloudflight.integra.backend.finalgrade.model.FinalGrade;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class EnrollmentRepository {
    private final Map<UUID, Enrollment> enrollments = new HashMap<>();

    public List<Enrollment> findAll() {
        return new ArrayList<>(enrollments.values());
    }

    public Optional<Enrollment> findById(UUID id) {
        return Optional.ofNullable(enrollments.get(id));
    }

    public Enrollment save(Enrollment enrollment) {
        if (enrollment.getId() == null) {
            enrollment.setId(UUID.randomUUID());
        }
        enrollments.put(enrollment.getId(), enrollment);
        return enrollment;
    }

    public void deleteById(UUID id) {
        enrollments.remove(id);
    }

    public List<Enrollment> getByCourseId(UUID courseId) {
        return enrollments.values().stream().filter(enrollment -> Objects.equals(enrollment.getCourseId(), courseId)).toList();
    }
}

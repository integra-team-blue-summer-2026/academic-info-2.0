package cloudflight.integra.backend.enrollment;

import cloudflight.integra.backend.enrollment.model.Enrollment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EnrollmentService {
    private final EnrollmentRepository repository;

    public EnrollmentService(EnrollmentRepository repository) {
        this.repository = repository;
    }

    public List<Enrollment> getAll() {
        return repository.findAll();
    }

    public Optional<Enrollment> getById(UUID id) {
        return repository.findById(id);
    }

    public Enrollment create(Enrollment enrollment) {
        return repository.save(enrollment);
    }

    public Optional<Enrollment> update(UUID id, Enrollment enrollment) {
        return repository.findById(id).map(existing -> {
            enrollment.setId(id);
            return repository.save(enrollment);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id).map(existing -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }

    public List<Enrollment> getByCourseId(UUID courseId) {
        return repository.getByCourseId(courseId);
    }
}

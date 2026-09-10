package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.finalgrade.FinalGradeRepository;
import cloudflight.integra.backend.finalgrade.model.FinalGrade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FinalGradeService {
    private final FinalGradeRepository repository;

    public FinalGradeService(FinalGradeRepository repository) {
        this.repository = repository;
    }

    public List<FinalGrade> getAll() {
        return repository.findAll();
    }

    public Optional<FinalGrade> getById(UUID id) {
        return repository.findById(id);
    }

    public FinalGrade create(FinalGrade grade) {
        return repository.save(grade);
    }

    public Optional<FinalGrade> update(UUID id, FinalGrade grade) {
        return repository.findById(id).map(existing -> {
            grade.setId(id);
            return repository.save(grade);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id).map(existing -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }

    public List<FinalGrade> getByStudentId(UUID  studentId) {
        return repository.findByStudentId(studentId);
    }

    public List<FinalGrade> getByCourseId(UUID  courseId) {
        return repository.findByCourseId(courseId);
    }

    public Optional<FinalGrade> findByStudentIdAndCourseId(UUID studentId, UUID courseId) {
        return repository.findByStudentIdAndCourseId(studentId, courseId);
    }
}

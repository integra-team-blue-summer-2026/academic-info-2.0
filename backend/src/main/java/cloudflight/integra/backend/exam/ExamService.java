package cloudflight.integra.backend.exam;

import cloudflight.integra.backend.exam.model.Exam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExamService {
    private final ExamRepository repository;

    public ExamService(ExamRepository repository) {
        this.repository = repository;
    }

    public List<Exam> getAll() {
        return repository.findAll();
    }

    public Optional<Exam> getById(UUID id) {
        return repository.findById(id);
    }

    public Exam create(Exam exam) {
        return repository.save(exam);
    }

    public Optional<Exam> update(UUID id, Exam exam) {
        return repository.findById(id).map(existing -> {
            exam.setId(id);
            return repository.save(exam);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id).map(existing -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }
}

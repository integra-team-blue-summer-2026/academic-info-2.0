package cloudflight.integra.backend.student;

import cloudflight.integra.backend.student.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> getAll() {
        return repository.findAll();
    }

    public Optional<Student> getById(UUID id) {
        return repository.findById(id);
    }

    public Student create(Student student) {
        return repository.save(student);
    }

    public Optional<Student> update(UUID id, Student student) {
        return repository.findById(id).map(existing -> {
            student.setId(id);
            return repository.save(student);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id).map(existing -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }
}

package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeacherService {

    private final TeacherRepository repository;

    public TeacherService(TeacherRepository repository) {
        this.repository = repository;
    }

    public List<Teacher> getAll() {
        return repository.findAll();
    }

    public Optional<Teacher> getById(UUID id) {
        return repository.findById(id);
    }

    public Teacher create(Teacher teacher) {
        return repository.save(teacher);
    }

    public Optional<Teacher> update(UUID id, Teacher teacher) {
        return repository.findById(id).map(existing -> {
            teacher.setId(id);
            return repository.save(teacher);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id).map(existing -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }
}

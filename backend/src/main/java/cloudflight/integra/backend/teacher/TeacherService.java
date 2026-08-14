package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeacherService {

    private final TeacherRepository repository;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;

    public TeacherService(TeacherRepository repository, PasswordGenerator passwordGenerator, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordGenerator = passwordGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Teacher> getAll() {
        return repository.findAll();
    }

    public Optional<Teacher> getById(UUID id) {
        return repository.findById(id);
    }

    public Teacher create(Teacher teacher) {
        teacher.setPasswordHash(passwordEncoder.encode(passwordGenerator.generate()));
        return repository.save(teacher);
    }

    public Optional<Teacher> update(UUID id, Teacher teacher) {
        return repository.findById(id).map(existing  -> {
            teacher.setId(id);
            teacher.setPasswordHash(existing.getPasswordHash());
            return repository.save(teacher);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id).map(existing -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }

    public Optional<String> regeneratePassword(UUID id) {
        return repository.findById(id).map(existing-> {
            String plainPassword = passwordGenerator.generate();
            existing.setPasswordHash(passwordEncoder.encode(plainPassword));
            repository.save(existing);
            return plainPassword;
        });
    }
}

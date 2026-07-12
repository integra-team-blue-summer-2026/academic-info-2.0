package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TeacherRepository {
    private final Map<UUID, Teacher> teachers = new HashMap<>();

    public List<Teacher> findAll() {
        return new ArrayList<>(teachers.values());
    }

    public Optional<Teacher> findById(UUID id) {
        return Optional.ofNullable(teachers.get(id));
    }

    public Teacher save(Teacher teacher) {
        if (teacher.getId() == null) {
            teacher.setId(UUID.randomUUID());
        }
        teachers.put(teacher.getId(), teacher);
        return teacher;
    }

    public void deleteById(UUID id) {
        teachers.remove(id);
    }
}

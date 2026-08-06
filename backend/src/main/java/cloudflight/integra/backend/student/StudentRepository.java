package cloudflight.integra.backend.student;

import cloudflight.integra.backend.student.model.Student;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class StudentRepository {
    private final Map<UUID, Student> students = new HashMap<>();

    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }

    public Optional<Student> findById(UUID id) {
        return Optional.ofNullable(students.get(id));
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(UUID.randomUUID());
        }
        students.put(student.getId(), student);
        return student;
    }

    public void deleteById(UUID id) {
        students.remove(id);
    }
}

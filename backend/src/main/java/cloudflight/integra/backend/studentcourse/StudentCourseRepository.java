package cloudflight.integra.backend.studentcourse;

import cloudflight.integra.backend.studentcourse.model.StudentCourse;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StudentCourseRepository {

    private final Map<UUID, StudentCourse> studentCourses = new HashMap<>();

    public List<StudentCourse> findAll() {
        return new ArrayList<>(studentCourses.values());
    }

    public Optional<StudentCourse> findById(UUID id) {
        return Optional.ofNullable(studentCourses.get(id));
    }

    public List<StudentCourse> findByCourseId(UUID courseId) {
        return studentCourses.values()
            .stream()
            .filter(studentCourse ->
                studentCourse.getCourseId().equals(courseId)
            )
            .toList();
    }

    public Optional<StudentCourse> findByCourseIdAndStudentId(
        UUID courseId,
        UUID studentId
    ) {
        return studentCourses.values()
            .stream()
            .filter(studentCourse ->
                studentCourse.getCourseId().equals(courseId)
                    && studentCourse.getStudentId().equals(studentId)
            )
            .findFirst();
    }

    public StudentCourse save(StudentCourse studentCourse) {
        if (studentCourse.getId() == null) {
            studentCourse.setId(UUID.randomUUID());
        }

        studentCourses.put(
            studentCourse.getId(),
            studentCourse
        );

        return studentCourse;
    }

    public void deleteById(UUID id) {
        studentCourses.remove(id);
    }

    public boolean deleteByCourseIdAndStudentId(
        UUID courseId,
        UUID studentId
    ) {
        Optional<StudentCourse> relation =
            findByCourseIdAndStudentId(courseId, studentId);

        if (relation.isEmpty()) {
            return false;
        }

        deleteById(relation.get().getId());

        return true;
    }
}

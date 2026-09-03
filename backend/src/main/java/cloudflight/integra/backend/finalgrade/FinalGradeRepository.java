package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.course.model.Course;
import cloudflight.integra.backend.finalgrade.model.FinalGrade;
import cloudflight.integra.backend.student.model.Student;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FinalGradeRepository {
    public static final UUID TEST_STUDENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final Map<UUID, FinalGrade> finalGrades = new HashMap<>();

    public List<FinalGrade> findAll() {
        return new ArrayList<>(finalGrades.values());
    }

    public Optional<FinalGrade> findById(UUID id) {
        return Optional.ofNullable(finalGrades.get(id));
    }

    public FinalGrade save(FinalGrade grade) {
        if (grade.getId() == null) {
            grade.setId(UUID.randomUUID());
        }
        finalGrades.put(grade.getId(), grade);
        return grade;
    }

    public void deleteById(UUID id) {
        finalGrades.remove(id);
    }

    public List<FinalGrade> findByStudentId(UUID studentId) {
        return finalGrades.values().stream()
            .filter(grade -> Objects.equals(grade.getStudentId(), studentId))
            .toList();
    }

    public List<FinalGrade> findByCourseId(UUID courseId) {
        return finalGrades.values().stream()
            .filter(grade -> Objects.equals(grade.getCourseId(), courseId))
            .toList();
    }

    public Optional<FinalGrade> findByStudentIdAndCourseId(UUID studentId, UUID courseId) {
        return finalGrades.values().stream()
            .filter(fg -> studentId.equals(fg.getStudentId()) && courseId.equals(fg.getCourseId()))
            .findFirst();
    }
}

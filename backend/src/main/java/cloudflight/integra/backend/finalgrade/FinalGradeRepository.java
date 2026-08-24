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
            .filter(grade -> Objects.equals(grade.getStudent().getId(), studentId))
            .toList();
    }

    // TEST FRONTEND ONLY
    @PostConstruct
    public void initMockData() {
        Student student = new Student();
        student.setId(TEST_STUDENT_ID);
        student.setFirstName("Ion");
        student.setLastName("Popescu");

        // Semestrul 1
        createGrade(student, "Arhitectura Sistemelor de Calcul", 5, 1, 9, false, "2024-02-10");
        createGrade(student, "Fundamentele Programarii", 6, 1, 10, false, "2024-02-12");
        createGrade(student, "Algebra Liniara si Geometrie", 5, 1, 8, false, "2024-02-15");

        // Semestrul 2
        createGrade(student, "Structuri de Date si Algoritmi", 6, 2, 4, false, "2024-06-20");
        createGrade(student, "Sisteme de Operare", 5, 2, 4, true, "2024-06-22");
        createGrade(student, "Programare Orientata Obiect", 6, 2, 10, false, "2024-06-25");

        // Semestrul 3
        createGrade(student, "Baze de Date", 5, 3, 10, false, "2025-02-08");
        createGrade(student, "Metode Avansate de Programare", 6, 3, 9, false, "2025-02-11");
        createGrade(student, "Retele de Calculatoare", 5, 3, 8, false, "2025-02-14");

        // Semestrul 4
        createGrade(student, "Ingineria Sistemelor Soft", 5, 4, 9, true, "2025-06-18");
        createGrade(student, "Sisteme de Gestiune a Bazelor de Date", 5, 4, 8, true, "2025-06-20");
        createGrade(student, "Inteligenta Artificiala", 6, 4, 10, false, "2025-06-24");
    }

    private void createGrade(Student student, String courseName, int credits, int semester, int gradeVal, boolean provisional, String completionDate) {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setCourseName(courseName);
        course.setCredits(credits);

        FinalGrade grade = new FinalGrade();
        grade.setId(UUID.randomUUID());
        grade.setStudent(student);
        grade.setCourse(course);
        grade.setSemester(semester);
        grade.setGrade(gradeVal);
        grade.setProvisional(provisional);
        grade.setCompletionDate(completionDate);

        save(grade);
    }
}

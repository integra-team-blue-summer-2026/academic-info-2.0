package cloudflight.integra.backend.studentexam;

import cloudflight.integra.backend.exam.ExamRepository;
import cloudflight.integra.backend.exam.model.Exam;
import cloudflight.integra.backend.student.StudentRepository;
import cloudflight.integra.backend.student.model.Student;
import cloudflight.integra.backend.studentexam.model.StudentExam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentExamService {

    private final StudentExamRepository studentExamRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;

    public StudentExamService(StudentExamRepository studentExamRepository,
                              ExamRepository examRepository,
                              StudentRepository studentRepository) {
        this.studentExamRepository = studentExamRepository;
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
    }

    public List<StudentExam> getAll() {
        return studentExamRepository.findAll();
    }

    public Optional<StudentExam> getById(UUID id) {
        return studentExamRepository.findById(id);
    }

    public List<StudentExam> getByCourseId(UUID courseId) {
        Exam exam = examRepository.findAll().stream()
            .filter(e -> courseId.equals(e.getCourseId()))
            .findFirst()
            .orElse(null);

        if (exam == null) {
            return List.of();
        }

        return studentExamRepository.findAll().stream()
            .filter(se -> exam.getId().equals(se.getExamId()))
            .toList();
    }

    public Student getStudentForExam(StudentExam studentExam) {
        if (studentExam == null || studentExam.getStudentId() == null) {
            return null;
        }
        return studentRepository.findById(studentExam.getStudentId()).orElse(null);
    }

    public StudentExam create(StudentExam studentExam) {
        if (studentExam.getId() == null) {
            studentExam.setId(UUID.randomUUID());
        }
        return studentExamRepository.save(studentExam);
    }

    public Optional<StudentExam> update(UUID id, StudentExam updated) {
        return studentExamRepository.findById(id).map(existing -> {
            existing.setSessionGrade(updated.getSessionGrade());
            existing.setResitGrade(updated.getResitGrade());
            existing.setGradeStatus(updated.getGradeStatus());
            return studentExamRepository.save(existing);
        });
    }

    public void delete(UUID id) {
        studentExamRepository.deleteById(id);
    }

    public List<StudentExam> getByExamId(UUID examId) {
        return studentExamRepository.findAll().stream()
            .filter(se -> examId.equals(se.getExamId()))
            .toList();
    }
}

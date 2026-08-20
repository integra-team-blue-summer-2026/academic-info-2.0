package cloudflight.integra.backend.studentcourse;

import cloudflight.integra.backend.studentcourse.model.StudentCourse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentCourseService {

    private final StudentCourseRepository repository;

    public StudentCourseService(
        StudentCourseRepository repository
    ) {
        this.repository = repository;
    }

    public List<StudentCourse> getAll() {
        return repository.findAll();
    }

    public Optional<StudentCourse> getById(UUID id) {
        return repository.findById(id);
    }

    public List<StudentCourse> getByCourseId(UUID courseId) {
        return repository.findByCourseId(courseId);
    }

    public StudentCourse create(StudentCourse studentCourse) {

        Optional<StudentCourse> existing =
            repository.findByCourseIdAndStudentId(
                studentCourse.getCourseId(),
                studentCourse.getStudentId()
            );

        if (existing.isPresent()) {
            return existing.get();
        }

        return repository.save(studentCourse);
    }

    public boolean delete(
        UUID courseId,
        UUID studentId
    ) {
        return repository.deleteByCourseIdAndStudentId(
            courseId,
            studentId
        );
    }
}

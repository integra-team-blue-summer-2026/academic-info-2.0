package cloudflight.integra.backend.enrollment;

import cloudflight.integra.backend.enrollment.model.Enrollment;
import cloudflight.integra.backend.enrollment.model.EnrollmentDto;
import cloudflight.integra.backend.student.StudentService;
import cloudflight.integra.backend.student.model.Student;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentController(EnrollmentService enrollmentService, StudentService studentService, EnrollmentMapper enrollmentMapper) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Operation(operationId = "getEnrollmentsByCourseId")
    @GetMapping(value = "course/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EnrollmentDto> getEnrollmentsByCourseId(@PathVariable UUID courseId) {
        return enrollmentService.getByCourseId(courseId).stream()
            .map(this::toDto)
            .toList();
    }

    private EnrollmentDto toDto(Enrollment enrollment) {
        Student student = studentService.getById(enrollment.getStudentId()).orElse(null);
        return enrollmentMapper.toDto(enrollment, student);
    }
}

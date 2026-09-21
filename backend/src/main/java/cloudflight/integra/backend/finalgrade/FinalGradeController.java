package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.course.CourseService;
import cloudflight.integra.backend.course.model.Course;
import cloudflight.integra.backend.enrollment.EnrollmentService;
import cloudflight.integra.backend.enrollment.model.Enrollment;
import cloudflight.integra.backend.finalgrade.FinalGradeService;
import cloudflight.integra.backend.finalgrade.model.FinalGrade;
import cloudflight.integra.backend.finalgrade.model.StudentFinalGradeDto;
import cloudflight.integra.backend.finalgrade.model.StudentGradeRowDto;
import cloudflight.integra.backend.finalgrade.model.TeacherFinalGradeDto;
import cloudflight.integra.backend.student.StudentService;
import cloudflight.integra.backend.student.model.Student;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/finalgrades")
public class FinalGradeController {

    private final FinalGradeService finalGradeService;
    private final CourseService courseService;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final StudentFinalGradeMapper studentFinalGradeMapper;
    private final TeacherFinalGradeMapper teacherFinalGradeMapper;
    private final GradingSheetMapper gradingSheetMapper;

    public FinalGradeController(FinalGradeService service, StudentFinalGradeMapper studentMapper, StudentService studentService, EnrollmentService enrollmentService,
                                TeacherFinalGradeMapper teacherFinalGradeMapper, CourseService courseService, GradingSheetMapper gradingSheetMapper
    ) {
        this.finalGradeService = service;
        this.studentFinalGradeMapper = studentMapper;
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
        this.teacherFinalGradeMapper = teacherFinalGradeMapper;
        this.courseService = courseService;
        this.gradingSheetMapper = gradingSheetMapper;
    }

    @Operation(operationId = "getFinalGradesByStudentId")
    @GetMapping(value = "/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StudentFinalGradeDto> getFinalGradesByStudentId(@PathVariable UUID studentId) {
        return finalGradeService.getByStudentId(studentId).stream()
            .map(this::toStudentFinalGradeDto)
            .toList();
    }

    private StudentFinalGradeDto toStudentFinalGradeDto(FinalGrade finalGrade) {
        Course course = courseService.getById(finalGrade.getCourseId()).orElse(null);
        return studentFinalGradeMapper.toDto(finalGrade, course);
    }

    @Operation(operationId = "createFinalGrade")
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public TeacherFinalGradeDto create(@RequestBody TeacherFinalGradeDto gradeDto) {
        Optional<FinalGrade> existing = finalGradeService.findByStudentIdAndCourseId(gradeDto.studentId(), gradeDto.courseId());

        FinalGrade result;
        if (existing.isPresent()) {
            UUID existingId = existing.get().getId();
            FinalGrade entity = teacherFinalGradeMapper.toEntity(gradeDto);
            entity.setId(existingId);

            result = finalGradeService.update(existingId, entity)
                .orElseThrow(() -> new RuntimeException("Error updating grade"));
        } else {
            result = finalGradeService.create(teacherFinalGradeMapper.toEntity(gradeDto));
        }

        return toTeacherFinalGradeDto(result);
    }

    private TeacherFinalGradeDto toTeacherFinalGradeDto(FinalGrade finalGrade) {
        Course course = courseService.getById(finalGrade.getCourseId()).orElse(null);
        Student student = studentService.getById(finalGrade.getStudentId()).orElse(null);
        return teacherFinalGradeMapper.toDto(finalGrade, course, student);
    }

    @Operation(operationId = "getGradingSheetByCourseId")
    @GetMapping(value = "/course/{courseId}/grading-sheet", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StudentGradeRowDto> getGradingSheet(@PathVariable UUID courseId) {
        List<Enrollment> enrollments = enrollmentService.getByCourseId(courseId);
        List<FinalGrade> grades = finalGradeService.getByCourseId(courseId);

        Map<UUID, FinalGrade> gradeMap = grades.stream()
            .collect(Collectors.toMap(FinalGrade::getStudentId, g -> g));

        return enrollments.stream().map(enrollment -> {
            FinalGrade grade = gradeMap.get(enrollment.getStudentId());

            Student student = studentService.getById(enrollment.getStudentId()).orElse(null);

            String studentFullName = "Unknown";
            String studentGroup = null;

            if (student != null) {
                String lastName = student.getLastName() != null ? student.getLastName() : "";
                String firstName = student.getFirstName() != null ? student.getFirstName() : "";
                studentFullName = (lastName + " " + firstName).trim();
                studentGroup = student.getGroup();
            }

            return gradingSheetMapper.toDto(enrollment, grade, studentGroup, studentFullName);
        }).toList();
    }
}

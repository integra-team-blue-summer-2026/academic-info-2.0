package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.course.CourseService;
import cloudflight.integra.backend.course.model.Course;
import cloudflight.integra.backend.enrollment.EnrollmentService;
import cloudflight.integra.backend.enrollment.model.Enrollment;
import cloudflight.integra.backend.finalgrade.model.*;
import cloudflight.integra.backend.student.StudentService;
import cloudflight.integra.backend.student.model.Student;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
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
        String currentYear = getCurrentAcademicYear();

        List<Enrollment> currentEnrollments = enrollmentService.getByCourseId(courseId).stream()
            .filter(enrollment -> Objects.equals(enrollment.getAcademicYear(), currentYear))
            .toList();

        List<FinalGrade> grades = finalGradeService.getByCourseId(courseId);

        Map<UUID, FinalGrade> gradeMap = grades.stream()
            .collect(Collectors.toMap(
                FinalGrade::getStudentId,
                g -> g,
                (existing, replacement) -> replacement
            ));

        return currentEnrollments.stream().map(enrollment -> {
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

    @Operation(operationId = "getCourseStatistics")
    @GetMapping(value = "/course/{courseId}/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<YearlyStatisticsDto> getCourseStatistics(@PathVariable UUID courseId) {
        List<FinalGrade> grades = finalGradeService.getByCourseId(courseId);

        Map<String, List<FinalGrade>> gradesByYear = grades.stream()
            .filter(g -> g.getGrade() != null)
            .collect(Collectors.groupingBy(g -> getAcademicYearFromCompletionDate(g.getCompletionDate())));

        return gradesByYear.entrySet().stream()
            .map(entry -> {
                String academicYear = entry.getKey();
                List<FinalGrade> yearGrades = entry.getValue();

                double avg = yearGrades.stream()
                    .mapToInt(FinalGrade::getGrade)
                    .average()
                    .orElse(0.0);

                long passedCount = yearGrades.stream()
                    .filter(g -> g.getGrade() >= 5)
                    .count();

                double passRate = yearGrades.isEmpty()
                    ? 0.0
                    : ((double) passedCount / yearGrades.size()) * 100.0;

                return new YearlyStatisticsDto(
                    academicYear,
                    Math.round(avg * 100.0) / 100.0,
                    Math.round(passRate * 10.0) / 10.0,
                    yearGrades.size()
                );
            })
            .sorted((a, b) -> b.academicYear().compareTo(a.academicYear()))
            .toList();
    }

    private String getAcademicYearFromCompletionDate(String completionDate) {
        if (completionDate == null || completionDate.isBlank()) {
            return getCurrentAcademicYear();
        }
        try {
            LocalDate date = LocalDate.parse(completionDate);
            int year = date.getYear();
            int month = date.getMonthValue();
            return (month >= 10) ? year + "/" + (year + 1) : (year - 1) + "/" + year;
        } catch (Exception e) {
            return getCurrentAcademicYear();
        }
    }

    private String getCurrentAcademicYear() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        return (month >= 10) ? year + "/" + (year + 1) : (year - 1) + "/" + year;
    }
}

package cloudflight.integra.backend.studentexam;

import cloudflight.integra.backend.exam.ExamService;
import cloudflight.integra.backend.exam.model.Exam;
import cloudflight.integra.backend.studentexam.model.ExamYearlyStatisticsDto;
import cloudflight.integra.backend.studentexam.model.StudentExam;
import cloudflight.integra.backend.studentexam.model.StudentExamDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/studentexams")
public class StudentExamController {

    private final StudentExamService studentExamService;
    private final ExamService examService;
    private final StudentExamMapper mapper;

    public StudentExamController(StudentExamService studentExamService, StudentExamMapper mapper, ExamService examService) {
        this.studentExamService = studentExamService;
        this.mapper = mapper;
        this.examService = examService;
    }

    private StudentExamDto toFullDto(StudentExam se) {
        return mapper.toDto(se, studentExamService.getStudentForExam(se));
    }

    @Operation(operationId = "getAllStudentExams")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StudentExamDto> getAll() {
        return studentExamService.getAll().stream()
            .map(this::toFullDto)
            .toList();
    }

    @Operation(operationId = "getStudentExamsByCourseId")
    @GetMapping(value = "/course/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StudentExamDto> getByCourseId(@PathVariable UUID courseId) {
        return studentExamService.getByCourseId(courseId).stream()
            .map(this::toFullDto)
            .toList();
    }

    @Operation(operationId = "getStudentExamById")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentExamDto getById(@PathVariable UUID id) {
        return studentExamService.getById(id)
            .map(this::toFullDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(operationId = "createStudentExam")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentExamDto> create(@RequestBody StudentExamDto dto) {
        StudentExam created = studentExamService.create(mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(toFullDto(created));
    }

    @Operation(operationId = "updateStudentExam")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentExamDto update(@PathVariable UUID id, @RequestBody StudentExamDto dto) {
        return studentExamService.update(id, mapper.toEntity(dto))
            .map(this::toFullDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(operationId = "deleteStudentExam")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        studentExamService.delete(id);
    }

    @Operation(operationId = "getCourseExamStatistics")
    @GetMapping(value = "/course/{courseId}/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExamYearlyStatisticsDto> getCourseStatistics(@PathVariable UUID courseId) {
        List<Exam> exams = examService.getByCourseId(courseId);

        return exams.stream()
            .map(exam -> {
                String academicYear = getAcademicYearFromDate(exam.getExamDate());
                List<StudentExam> studentExams = studentExamService.getByExamId(exam.getId());
                int total = studentExams.size();

                if (total == 0) {
                    return new ExamYearlyStatisticsDto(academicYear, 0.0, 0.0, 0.0, 0.0, 0.0, 0);
                }

                List<Double> sessionGrades = studentExams.stream()
                    .map(s -> parseGrade(s.getSessionGrade()))
                    .filter(Objects::nonNull)
                    .toList();

                double sessionAvg = sessionGrades.stream().mapToDouble(d -> d).average().orElse(0.0);
                double sessionPass = ((double) sessionGrades.stream().filter(g -> g >= 5.0).count() / total) * 100.0;

                List<Double> resitGrades = studentExams.stream()
                    .map(s -> parseGrade(s.getResitGrade()))
                    .filter(Objects::nonNull)
                    .toList();

                double resitAvg = resitGrades.stream().mapToDouble(d -> d).average().orElse(0.0);
                double resitPass = resitGrades.isEmpty()
                    ? 0.0
                    : ((double) resitGrades.stream().filter(g -> g >= 5.0).count() / resitGrades.size()) * 100.0;

                long passedTotal = studentExams.stream()
                    .filter(s -> (parseGrade(s.getSessionGrade()) != null && parseGrade(s.getSessionGrade()) >= 5.0)
                        || (parseGrade(s.getResitGrade()) != null && parseGrade(s.getResitGrade()) >= 5.0))
                    .count();
                double overhaulPass = ((double) passedTotal / total) * 100.0;

                return new ExamYearlyStatisticsDto(
                    academicYear,
                    Math.round(sessionAvg * 10.0) / 10.0,
                    Math.round(sessionPass * 10.0) / 10.0,
                    Math.round(resitAvg * 10.0) / 10.0,
                    Math.round(resitPass * 10.0) / 10.0,
                    Math.round(overhaulPass * 10.0) / 10.0,
                    total
                );
            })
            .sorted((a, b) -> b.academicYear().compareTo(a.academicYear()))
            .toList();
    }

    private Double parseGrade(String val) {
        try {
            return (val != null && !val.isBlank()) ? Double.parseDouble(val.trim()) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getAcademicYearFromDate(String examDate) {
        if (examDate == null || examDate.isBlank()) {
            return "2025/2026";
        }
        try {
            LocalDate d = LocalDate.parse(examDate);
            int y = d.getYear();
            return (d.getMonthValue() >= 10) ? y + "/" + (y + 1) : (y - 1) + "/" + y;
        } catch (Exception e) {
            return "2025/2026";
        }
    }
}

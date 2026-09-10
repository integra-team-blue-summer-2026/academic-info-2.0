package cloudflight.integra.backend.finalgrade;

import cloudflight.integra.backend.exam.FinalGradeService;
import cloudflight.integra.backend.finalgrade.model.StudentFinalGradeDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/finalgrades")
public class FinalGradeController {

    private final FinalGradeService service;
    private final StudentFinalGradeMapper studentMapper;

    public FinalGradeController(FinalGradeService service, StudentFinalGradeMapper studentMapper) {
        this.service = service;
        this.studentMapper = studentMapper;
    }

    @Operation(operationId = "getFinalGradesByStudentId")
    @GetMapping(value = "/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StudentFinalGradeDto> getFinalGradesByStudentId(@PathVariable UUID studentId) {
        return service.getByStudentId(studentId).stream()
            .map(studentMapper::toDto)
            .toList();
    }
}

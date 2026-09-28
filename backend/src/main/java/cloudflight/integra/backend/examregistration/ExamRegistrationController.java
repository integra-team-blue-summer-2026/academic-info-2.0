package cloudflight.integra.backend.examregistration;

import cloudflight.integra.backend.examregistration.model.ExamRegistrationDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/exam-registrations")
public class ExamRegistrationController {
    private final ExamRegistrationService service;
    private final ExamRegistrationMapper mapper;

    public ExamRegistrationController(ExamRegistrationService service, ExamRegistrationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(operationId = "getAllRegistrations")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExamRegistrationDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @Operation(operationId = "getRegistrationsByStudent")
    @GetMapping(value = "/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExamRegistrationDto> getByStudent(@PathVariable UUID studentId) {
        return service.getByStudent(studentId).stream().map(mapper::toDto).toList();
    }

    @Operation(operationId = "createRegistration")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamRegistrationDto> create(@RequestBody ExamRegistrationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toDto(service.register(mapper.toEntity(dto))));
    }

    @Operation(operationId = "deleteRegistration")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

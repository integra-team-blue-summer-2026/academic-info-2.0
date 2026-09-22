package cloudflight.integra.backend.advisorrequest;

import cloudflight.integra.backend.advisorrequest.model.AdvisorRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/advisor-requests")
public class AdvisorRequestController {
    private final AdvisorRequestService service;
    private final AdvisorRequestMapper mapper;

    public AdvisorRequestController(AdvisorRequestService service, AdvisorRequestMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(operationId = "getRequestsByStudent")
    @GetMapping(value = "/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AdvisorRequestDto> getByStudent(@PathVariable UUID studentId) {
        return service.getByStudent(studentId).stream().map(mapper::toDto).toList();
    }

    @Operation(operationId = "getRequestsByTeacher")
    @GetMapping(value = "/teacher/{teacherId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AdvisorRequestDto> getByTeacher(@PathVariable UUID teacherId) {
        return service.getByTeacher(teacherId).stream().map(mapper::toDto).toList();
    }

    @Operation(operationId = "applyForAdvisor")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdvisorRequestDto> apply(@RequestBody AdvisorRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toDto(service.apply(mapper.toEntity(dto))));
    }

    @Operation(operationId = "approveRequest")
    @PutMapping(value = "/{id}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public AdvisorRequestDto approve(@PathVariable UUID id) {
        return mapper.toDto(service.approve(id));
    }

    @Operation(operationId = "rejectRequest")
    @PutMapping(value = "/{id}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public AdvisorRequestDto reject(@PathVariable UUID id) {
        return mapper.toDto(service.reject(id));
    }
}

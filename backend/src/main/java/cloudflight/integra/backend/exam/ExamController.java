package cloudflight.integra.backend.exam;

import cloudflight.integra.backend.exam.model.ExamDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService service;
    private final ExamMapper mapper;

    public ExamController(ExamService service, ExamMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(operationId = "getAllExams")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExamDto> getAll() {
        return  service.getAll().stream().map(mapper::toDto).toList();
    }

    @Operation(operationId = "getExamById")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExamDto getById(@PathVariable UUID id) {
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(operationId = "createExam")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExamDto> create(@RequestBody ExamDto examDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(examDto))));
    }

    @Operation(operationId = "updateExam")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExamDto update(@PathVariable UUID id, @RequestBody ExamDto dto) {
        return service.update(id, mapper.toEntity(dto)).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(operationId = "deleteExam")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

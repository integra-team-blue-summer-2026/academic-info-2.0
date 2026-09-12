package cloudflight.integra.backend.student;

import cloudflight.integra.backend.student.model.StudentDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;
    private final StudentMapper mapper;

    public StudentController(StudentService service, StudentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(operationId = "getAllStudents")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StudentDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentDto getById(@PathVariable UUID id) {
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(operationId = "createStudent")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StudentDto> create(@RequestBody StudentDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(dto))));
    }

    @Operation(operationId = "updateStudent")
    @PutMapping("/{id}")
    public StudentDto update(@PathVariable UUID id, @RequestBody StudentDto dto) {
        return service.update(id, mapper.toEntity(dto)).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(operationId = "deleteStudent")
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

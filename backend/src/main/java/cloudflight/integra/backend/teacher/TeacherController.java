package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.GeneratePasswordDto;
import cloudflight.integra.backend.teacher.model.TeacherDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService service;
    private final TeacherMapper mapper;

    public TeacherController(TeacherService service, TeacherMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(operationId = "getAllTeachers")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TeacherDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @Operation(operationId = "getTeacherById")
    @GetMapping("/{id}")
    public TeacherDto getById(@PathVariable UUID id) {
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(operationId = "addTeacher")
    @PostMapping
    public ResponseEntity<TeacherDto> create(@RequestBody TeacherDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Request body must not be null"
            );
        }

        TeacherDto dtoWithoutId = new TeacherDto(
            null,
            dto.firstName(),
            dto.lastName(),
            dto.title(),
            dto.department()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toDto(service.create(mapper.toEntity(dtoWithoutId))));
    }

    @Operation(operationId = "updateTeacher")
    @PutMapping("/{id}")
    public TeacherDto update(@PathVariable UUID id, @RequestBody TeacherDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Request body must not be null"
            );
        }

        return service.update(id, mapper.toEntity(dto))
            .map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(operationId = "deleteTeacher")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (!service.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.noContent().build();
    }

    @Operation(operationId = "regenerateTeacherPassword")
    @PostMapping("/{id}/password/regenerate")
    public GeneratePasswordDto regeneratePassword(@PathVariable UUID id){
        return service.regeneratePassword(id).map(GeneratePasswordDto::new).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}

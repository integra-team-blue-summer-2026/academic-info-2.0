package cloudflight.integra.backend.teacher.availability;

import cloudflight.integra.backend.teacher.availability.model.TeacherAvailability;
import cloudflight.integra.backend.teacher.availability.model.TeacherAvailabilityDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teacher-availability")
public class TeacherAvailabilityController {

    private final TeacherAvailabilityService service;
    private final TeacherAvailabilityMapper mapper;

    public TeacherAvailabilityController(
        TeacherAvailabilityService service,
        TeacherAvailabilityMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(operationId = "getTeacherAvailability")
    @GetMapping(
        value = "/teacher/{teacherId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<TeacherAvailabilityDto> getByTeacherId(
        @PathVariable UUID teacherId
    ) {
        return service.getByTeacherId(teacherId)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Operation(operationId = "getTeacherAvailabilityById")
    @GetMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TeacherAvailabilityDto getById(
        @PathVariable UUID id
    ) {
        return service.getById(id)
            .map(mapper::toDto)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    @Operation(operationId = "createTeacherAvailability")
    @PostMapping
    public ResponseEntity<TeacherAvailabilityDto> create(
        @RequestBody TeacherAvailabilityDto dto
    ) {
        if (dto == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Request body must not be null"
            );
        }

        TeacherAvailability entity = mapper.toEntity(dto);

        /*
         * ID-ul este generat de repository.
         */
        entity.setId(null);

        TeacherAvailability saved = service.create(entity);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapper.toDto(saved));
    }

    @Operation(operationId = "updateTeacherAvailability")
    @PutMapping("/{id}")
    public TeacherAvailabilityDto update(
        @PathVariable UUID id,
        @RequestBody TeacherAvailabilityDto dto
    ) {
        if (dto == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Request body must not be null"
            );
        }

        return service.update(id, mapper.toEntity(dto))
            .map(mapper::toDto)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    @Operation(operationId = "deleteTeacherAvailability")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID id
    ) {
        if (!service.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.noContent().build();
    }
}

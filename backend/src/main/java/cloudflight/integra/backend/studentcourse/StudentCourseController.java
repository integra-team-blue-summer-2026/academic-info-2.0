package cloudflight.integra.backend.studentcourse;

import cloudflight.integra.backend.studentcourse.model.StudentCourseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-courses")
public class StudentCourseController {

    private final StudentCourseService service;
    private final StudentCourseMapper mapper;

    public StudentCourseController(
        StudentCourseService service,
        StudentCourseMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StudentCourseDto> getAll() {
        return service.getAll()
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public StudentCourseDto getById(
        @PathVariable UUID id
    ) {
        return service.getById(id)
            .map(mapper::toDto)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND
                )
            );
    }

    @GetMapping(value="/course/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StudentCourseDto> getByCourseId(
        @PathVariable UUID courseId
    ) {
        return service.getByCourseId(courseId)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @PostMapping
    public ResponseEntity<StudentCourseDto> create(
        @RequestBody StudentCourseDto dto
    ) {
        StudentCourseDto created =
            mapper.toDto(
                service.create(
                    mapper.toEntity(dto)
                )
            );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

    @DeleteMapping("/{courseId}/{studentId}")
    public void delete(
        @PathVariable UUID courseId,
        @PathVariable UUID studentId
    ) {
        boolean deleted =
            service.delete(courseId, studentId);

        if (!deleted) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND
            );
        }
    }
}

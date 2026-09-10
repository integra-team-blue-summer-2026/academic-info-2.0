package cloudflight.integra.backend.course;

import cloudflight.integra.backend.course.model.CourseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseController {

    private final CourseService service;
    private final CourseMapper mapper;

    public CourseController(CourseService service, CourseMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(operationId = "getAllCourses")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "All courses",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(
                    schema = @Schema(implementation = CourseDto.class)
                )
            )
        )
    })
    public List<CourseDto> getAll() {
        return service.getAll()
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Operation(operationId = "getCourseById")
    @GetMapping(
        value = "/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Course found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CourseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Course not found"
        )
    })
    public CourseDto getById(@PathVariable UUID id) {
        return service.getById(id)
            .map(mapper::toDto)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    @Operation(operationId = "createCourse")
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Course created",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CourseDto.class)
            )
        )
    })
    public ResponseEntity<CourseDto> create(
        @RequestBody CourseDto dto
    ) {
        CourseDto created = mapper.toDto(
            service.create(mapper.toEntity(dto))
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

    @Operation(operationId = "updateCourse")
    @PutMapping(
        value = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Course updated",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CourseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Course not found"
        )
    })
    public CourseDto update(
        @PathVariable UUID id,
        @RequestBody CourseDto dto
    ) {
        return service.update(id, mapper.toEntity(dto))
            .map(mapper::toDto)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    @Operation(operationId = "deleteCourse")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Course deleted"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Course not found"
        )
    })
    public void delete(@PathVariable UUID id) {
        if (!service.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

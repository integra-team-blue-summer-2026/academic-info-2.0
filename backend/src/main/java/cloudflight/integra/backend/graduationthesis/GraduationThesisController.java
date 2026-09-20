package cloudflight.integra.backend.graduationthesis;

import cloudflight.integra.backend.graduationthesis.model.GraduationThesis;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
    value = "/api/graduation-theses",
    produces = MediaType.APPLICATION_JSON_VALUE
)
@CrossOrigin(origins = "http://localhost:4200")
public class GraduationThesisController {

    private final GraduationThesisService service;
    private final GraduationThesisMapper mapper;

    public GraduationThesisController(
        GraduationThesisService service,
        GraduationThesisMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/advisor/{advisorId}")
    public List<GraduationThesisDto> getByAdvisorId(@PathVariable UUID advisorId) {
        return service.getByAdvisorId(advisorId)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GraduationThesisDto> getById(@PathVariable UUID id) {
        return service.getById(id)
            .map(mapper::toDto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GraduationThesisDto> create(
        @RequestBody GraduationThesisDto dto
    ) {
        GraduationThesis thesis = mapper.toEntity(dto);
        GraduationThesis created = service.create(thesis);

        return ResponseEntity.status(201)
            .body(mapper.toDto(created));
    }

    @PutMapping("/{id}/admit")
    public ResponseEntity<GraduationThesisDto> admit(@PathVariable UUID id) {
        return service.admit(id)
            .map(mapper::toDto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<GraduationThesisDto> reject(
        @PathVariable UUID id,
        @RequestBody String message
    ) {
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return service.reject(id, message)
            .map(mapper::toDto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

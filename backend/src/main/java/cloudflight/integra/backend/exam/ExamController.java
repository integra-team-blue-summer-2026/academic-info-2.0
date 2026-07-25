package cloudflight.integra.backend.exam;

import cloudflight.integra.backend.exam.model.ExamDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService service;
    private final ExamMapper mapper;

    public ExamController(ExamService service, ExamMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<ExamDto> getAll() {
        return  service.getAll().stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ExamDto getById(@PathVariable UUID id) {
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ExamDto> create(@RequestBody ExamDto examDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(examDto))));
    }

    @PutMapping("/{id}")
    public ExamDto update(@PathVariable UUID id, @RequestBody ExamDto dto) {
        return service.update(id, mapper.toEntity(dto)).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

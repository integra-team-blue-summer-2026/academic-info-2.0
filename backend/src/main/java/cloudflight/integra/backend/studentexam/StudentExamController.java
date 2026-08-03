package cloudflight.integra.backend.studentexam;


import cloudflight.integra.backend.studentexam.model.StudentExamDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/studentexams")
public class StudentExamController {
    private final StudentExamService service;
    private final StudentExamMapper mapper;

    public StudentExamController(StudentExamService service, StudentExamMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<StudentExamDto> getAll() { return service.getAll().stream().map(mapper::toDto).toList(); }

    @GetMapping("/{id}")
    public StudentExamDto getById(@PathVariable UUID id){
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<StudentExamDto> create(@RequestBody StudentExamDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(dto))));
    }

    @PutMapping("/{id}")
    public StudentExamDto update(@PathVariable UUID id, @RequestBody StudentExamDto dto) {
        return service.update(id, mapper.toEntity(dto)).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) { service.delete(id); }
}

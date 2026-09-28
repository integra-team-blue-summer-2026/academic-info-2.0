package cloudflight.integra.backend.thesis;

import cloudflight.integra.backend.thesis.model.Thesis;
import cloudflight.integra.backend.thesis.model.ThesisDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/thesis")
public class ThesisController {

    private final ThesisService service;
    private final ThesisMapper mapper;

    public ThesisController(
        ThesisService service,
        ThesisMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(operationId = "getThesisByStudent")
    @GetMapping(value="/student/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ThesisDto> getByStudent(
        @PathVariable UUID studentId
    ) {
        return service.getByStudentId(studentId)
            .map(thesis -> ResponseEntity.ok(mapper.toDto(thesis)))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(operationId = "uploadThesis")
    @PostMapping(
        value = "/student/{studentId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ThesisDto> upload(
        @PathVariable UUID studentId,
        @RequestParam("file") MultipartFile file
    ) {
        try {
            Thesis thesis = service.upload(studentId, file);
            return ResponseEntity.ok(mapper.toDto(thesis));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
            );
        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to save thesis file."
            );
        }
    }
    @Operation(operationId = "downloadThesis")
    @GetMapping(value="/{id}/file", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource> download(
        @PathVariable UUID id
    ) {
        try {
            Path path = service.getFile(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Thesis not found."
                ));

            if (!java.nio.file.Files.exists(path)) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Thesis file not found."
                );
            }

            Resource resource = new UrlResource(path.toUri());

            String fileName = resource.getFilename();

            HttpHeaders headers = new HttpHeaders();

            if (fileName != null) {
                headers.setContentDisposition(
                    ContentDisposition.inline()
                        .filename(fileName)
                        .build()
                );
            }

            headers.setContentType(MediaType.APPLICATION_PDF);

            return new ResponseEntity<>(
                resource,
                headers,
                HttpStatus.OK
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to read thesis file."
            );
        }
    }
}

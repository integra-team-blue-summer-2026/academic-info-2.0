package cloudflight.integra.backend.thesis;

import cloudflight.integra.backend.student.StudentRepository;
import cloudflight.integra.backend.thesis.model.Thesis;
import cloudflight.integra.backend.thesis.model.ThesisStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ThesisService {

    private final ThesisRepository thesisRepository;
    private final StudentRepository studentRepository;

    private final Path uploadDirectory = Paths.get("uploads/theses");

    public ThesisService(
        ThesisRepository thesisRepository,
        StudentRepository studentRepository
    ) {
        this.thesisRepository = thesisRepository;
        this.studentRepository = studentRepository;
    }

    public Optional<Thesis> getByStudentId(UUID studentId) {
        return thesisRepository.findByStudentId(studentId);
    }

    public Thesis upload(UUID studentId, MultipartFile file) throws IOException {

        if (!studentRepository.findById(studentId).isPresent()) {
            throw new IllegalArgumentException("Student not found.");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A thesis file is required.");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name.");
        }

        String lowerCaseFileName = originalFileName.toLowerCase();

        if (!lowerCaseFileName.endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed.");
        }

        Files.createDirectories(uploadDirectory);

        Thesis thesis = thesisRepository
            .findByStudentId(studentId)
            .orElseGet(Thesis::new);

        UUID thesisId = thesis.getId() != null
            ? thesis.getId()
            : UUID.randomUUID();

        thesis.setId(thesisId);
        thesis.setStudentId(studentId);

        String storedFileName = thesisId + ".pdf";
        Path targetPath = uploadDirectory.resolve(storedFileName);

        Files.copy(
            file.getInputStream(),
            targetPath,
            StandardCopyOption.REPLACE_EXISTING
        );

        thesis.setFileName(originalFileName);
        thesis.setFilePath(targetPath.toString());
        thesis.setStatus(ThesisStatus.UNCHECKED);
        thesis.setRejectionMessage(null);
        thesis.setUploadedAt(LocalDateTime.now());
        thesis.setCheckedAt(null);

        return thesisRepository.save(thesis);
    }

    public Optional<Path> getFile(UUID thesisId) {
        return thesisRepository
            .findById(thesisId)
            .map(Thesis::getFilePath)
            .map(Paths::get);
    }
}

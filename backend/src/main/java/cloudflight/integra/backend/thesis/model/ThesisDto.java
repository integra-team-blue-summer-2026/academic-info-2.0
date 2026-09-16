package cloudflight.integra.backend.thesis.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record ThesisDto(
    UUID id,
    UUID studentId,
    String fileName,
    ThesisStatus status,
    String rejectionMessage,
    LocalDateTime uploadedAt,
    LocalDateTime checkedAt
) {
}

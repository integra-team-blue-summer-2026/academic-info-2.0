package cloudflight.integra.backend.graduationthesis;

import cloudflight.integra.backend.graduationthesis.model.ThesisStatus;

import java.util.UUID;

public record GraduationThesisDto(
    UUID id,
    UUID studentId,
    UUID advisorId,
    String title,
    String fileName,
    ThesisStatus status,
    String rejectionMessage
) {
}

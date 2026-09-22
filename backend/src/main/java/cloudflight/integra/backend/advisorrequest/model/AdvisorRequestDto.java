package cloudflight.integra.backend.advisorrequest.model;

import java.util.UUID;

public record AdvisorRequestDto(UUID id, UUID studentId, UUID teacherId, String message, AdvisorRequestStatus status) {
}

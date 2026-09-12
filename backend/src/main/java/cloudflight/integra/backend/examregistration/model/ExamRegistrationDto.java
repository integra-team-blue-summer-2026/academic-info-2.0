package cloudflight.integra.backend.examregistration.model;

import java.util.UUID;

public record ExamRegistrationDto(UUID id, UUID studentId, UUID examId, ExamSlot chosenSlot) {}

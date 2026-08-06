package cloudflight.integra.backend.student.model;

import java.util.UUID;

public record StudentDto(
    UUID id,
    String nationalId,
    String firstName,
    String lastName,
    String dateOfBirth,
    String email
) {}

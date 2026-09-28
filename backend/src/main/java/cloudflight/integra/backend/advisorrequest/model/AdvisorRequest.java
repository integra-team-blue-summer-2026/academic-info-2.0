package cloudflight.integra.backend.advisorrequest.model;

import java.util.UUID;

public class AdvisorRequest {
    private UUID id;
    private UUID studentId;
    private UUID teacherId;
    private String message;
    private AdvisorRequestStatus status;

    public AdvisorRequest() {
    }

    public AdvisorRequest(UUID id, UUID studentId, UUID teacherId, String message, AdvisorRequestStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.message = message;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }

    public UUID getTeacherId() { return teacherId; }
    public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public AdvisorRequestStatus getStatus() { return status; }
    public void setStatus(AdvisorRequestStatus status) { this.status = status; }
}

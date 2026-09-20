package cloudflight.integra.backend.graduationthesis.model;

import java.util.UUID;

public class GraduationThesis {

    private UUID id;
    private UUID studentId;
    private UUID advisorId;
    private String title;
    private String fileName;
    private ThesisStatus status;
    private String rejectionMessage;

    public GraduationThesis() {
    }

    public GraduationThesis(UUID id, UUID studentId, UUID advisorId, String title,
                            String fileName, ThesisStatus status, String rejectionMessage) {
        this.id = id;
        this.studentId = studentId;
        this.advisorId = advisorId;
        this.title = title;
        this.fileName = fileName;
        this.status = status;
        this.rejectionMessage = rejectionMessage;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public UUID getAdvisorId() {
        return advisorId;
    }

    public void setAdvisorId(UUID advisorId) {
        this.advisorId = advisorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ThesisStatus getStatus() {
        return status;
    }

    public void setStatus(ThesisStatus status) {
        this.status = status;
    }

    public String getRejectionMessage() {
        return rejectionMessage;
    }

    public void setRejectionMessage(String rejectionMessage) {
        this.rejectionMessage = rejectionMessage;
    }
}

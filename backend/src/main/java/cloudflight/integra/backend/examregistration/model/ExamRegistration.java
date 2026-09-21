package cloudflight.integra.backend.examregistration.model;

import java.util.UUID;

public class ExamRegistration {
    private UUID id;
    private UUID studentId;
    private UUID examId;
    private ExamSlot chosenSlot;

    public ExamRegistration() {
    }

    public ExamRegistration(UUID id, UUID studentId, UUID examId, ExamSlot chosenSlot) {
        this.id = id;
        this.studentId = studentId;
        this.examId = examId;
        this.chosenSlot = chosenSlot;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }

    public UUID getExamId() { return examId; }
    public void setExamId(UUID examId) { this.examId = examId; }

    public ExamSlot getChosenSlot() { return chosenSlot; }
    public void setChosenSlot(ExamSlot chosenSlot) { this.chosenSlot = chosenSlot; }
}

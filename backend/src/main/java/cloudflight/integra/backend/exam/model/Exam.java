package cloudflight.integra.backend.exam.model;
import java.util.UUID;

public class Exam {
    private UUID id;
    private UUID courseId;
    private String examType;
    private String examDate;
    private String room;

    public Exam() {
    }

    public Exam(UUID id, UUID courseId, String examType, String examDate, String room) {
        this.id = id;
        this.courseId = courseId;
        this.examType = examType;
        this.examDate = examDate;
        this.room = room;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

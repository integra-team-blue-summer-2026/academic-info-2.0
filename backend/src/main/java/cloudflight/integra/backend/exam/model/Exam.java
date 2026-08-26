package cloudflight.integra.backend.exam.model;
import java.time.LocalDateTime;
import java.util.UUID;

public class Exam {
    private UUID id;
    private UUID courseId;
    private UUID teacherId;
    private ExamType examType;
    private ExamFormat examFormat;
    private String group;
    private int duration;
    private LocalDateTime primaryDate;
    private LocalDateTime secondaryDate;
    private String room;



    public Exam() {
    }

    public Exam(UUID id, UUID courseId, UUID teacherId, ExamType examType,  ExamFormat examFormat, String group, int duration, LocalDateTime primaryDate, LocalDateTime secondaryDate, String room) {
        this.id = id;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.examType = examType;
        this.examFormat = examFormat;
        this.group = group;
        this.duration = duration;
        this.primaryDate = primaryDate;
        this.secondaryDate = secondaryDate;
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

    public ExamType getExamType() {
        return examType;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    public LocalDateTime getPrimaryDate() {
        return primaryDate;
    }

    public void setPrimaryDate(LocalDateTime primaryDate) {
        this.primaryDate = primaryDate;
    }

    public UUID getTeacherId() { return teacherId; }

    public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }

    public ExamFormat getExamFormat() { return examFormat; }

    public void setExamFormat(ExamFormat examFormat) { this.examFormat = examFormat; }

    public String getGroup() { return group; }

    public void setGroup(String group) { this.group = group; }

    public int getDuration() { return duration; }

    public void setDuration(int duration) { this.duration = duration; }

    public LocalDateTime getSecondaryDate() { return secondaryDate; }

    public void setSecondaryDate(LocalDateTime secondaryDate) { this.secondaryDate = secondaryDate; }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

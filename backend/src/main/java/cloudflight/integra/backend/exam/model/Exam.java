package cloudflight.integra.backend.exam.model;

public class Exam {
    private Long id;
    private Long courseId;
    private String examType;
    private String examDate;
    private String room;

    public Exam() {
    }

    public Exam(Long id, Long courseId, String examType, String examDate, String room) {
        this.id = id;
        this.courseId = courseId;
        this.examType = examType;
        this.examDate = examDate;
        this.room = room;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
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

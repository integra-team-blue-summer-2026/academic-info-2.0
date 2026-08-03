package cloudflight.integra.backend.studentexam.model;

import java.util.UUID;

public class StudentExam {
    private UUID id;
    private UUID studentId;
    private UUID examId;
    private String sessionGrade;
    private String resitGrade;
    private GradeStatus gradeStatus;

   public StudentExam(){
   }

   public StudentExam(UUID id, UUID studentId, UUID examId, String sessionGrade, String resitGrade, GradeStatus gradeStatus){
       this.id = id;
       this.examId = examId;
       this.studentId = studentId;
       this.sessionGrade = sessionGrade;
       this.resitGrade = resitGrade;
       this.gradeStatus = gradeStatus;
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

    public UUID getExamId() {
       return examId;
    }

    public void setExamId(UUID examId) {
       this.examId = examId;
    }

    public String getSessionGrade() {
       return sessionGrade;
    }

    public void setSessionGrade(String sessionGrade) {
       this.sessionGrade = sessionGrade;
    }

    public String getResitGrade() {
       return resitGrade;
    }

    public void setResitGrade(String resitGrade) {
       this.resitGrade = resitGrade;
    }

    public GradeStatus getGradeStatus() {
       return gradeStatus;
    }

    public void setGradeStatus(GradeStatus gradeStatus) {
       this.gradeStatus = gradeStatus;
    }
}

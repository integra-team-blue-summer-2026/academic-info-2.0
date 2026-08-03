package cloudflight.integra.backend.studentexam;

import cloudflight.integra.backend.studentexam.model.StudentExam;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class StudentExamRepository {
    private final Map<UUID, StudentExam> studentExams = new HashMap<>();

    public List<StudentExam> findAll(){
        return new ArrayList<>(studentExams.values());
    }

    public Optional<StudentExam> findById(UUID id){
        return Optional.ofNullable(studentExams.get(id));
    }

    public StudentExam save(StudentExam studentExam){
        if(studentExam.getId() == null){
            studentExam.setId(UUID.randomUUID());
        }
        studentExams.put(studentExam.getId(), studentExam);
        return studentExam;
    }

    public void deleteById(UUID id){
        studentExams.remove(id);
    }
}

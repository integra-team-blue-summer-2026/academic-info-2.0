package cloudflight.integra.backend.studentexam;

import cloudflight.integra.backend.studentexam.model.StudentExam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentExamService {
    private final StudentExamRepository repository;

    public StudentExamService(StudentExamRepository repository) {
        this.repository = repository;
    }

    public List<StudentExam> getAll() {
        return new ArrayList<>(repository.findAll());
    }

    public Optional<StudentExam> getById(UUID id){
        return repository.findById(id);
    }

    public StudentExam create(StudentExam studentExam) {
        return repository.save(studentExam);
    }

    public Optional<StudentExam> update(UUID id, StudentExam studentExam) {
        return repository.findById(id).map(existing->{
            studentExam.setId(id);
            return repository.save(studentExam);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id).map(existing ->{
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }


}

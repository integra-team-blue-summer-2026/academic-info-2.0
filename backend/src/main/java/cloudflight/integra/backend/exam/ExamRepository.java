package cloudflight.integra.backend.exam;


import cloudflight.integra.backend.exam.model.Exam;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ExamRepository {
    private final Map<Long, Exam> exams = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<Exam> findAll() {
        return new ArrayList<>(exams.values());
    }

    public Optional<Exam> findById(Long id) {
        return Optional.ofNullable(exams.get(id));
    }

    public Exam save(Exam exam) {
        if (exam.getId() == null) {
            exam.setId(idGen.getAndIncrement());
        }
        exams.put(exam.getId(), exam);
        return exam;
    }

    public void deleteById(Long id) {
        exams.remove(id);
    }
}

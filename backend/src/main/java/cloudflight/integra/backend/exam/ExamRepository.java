package cloudflight.integra.backend.exam;

import cloudflight.integra.backend.exam.model.Exam;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ExamRepository {

    private final Map<UUID, Exam> exams = new HashMap<>();

    public List<Exam> findAll() {
        return new ArrayList<>(exams.values());
    }

    public Optional<Exam> findById(UUID id) {
        return Optional.ofNullable(exams.get(id));
    }

    public Exam save(Exam exam) {
        if (exam.getId() == null) {
            exam.setId(UUID.randomUUID());
        }

        exams.put(exam.getId(), exam);

        return exam;
    }

    public void deleteById(UUID id) {
        exams.remove(id);
    }

    public List<Exam> findByGroup(String group) {
        return exams.values().stream()
            .filter(e -> group.equals(e.getGroup())).toList();
    }

    public List<Exam> findByRoom(String room) {
        return exams.values().stream()
            .filter(e -> room.equals(e.getRoom())).toList();
    }
}

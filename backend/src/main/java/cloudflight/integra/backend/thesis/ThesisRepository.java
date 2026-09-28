package cloudflight.integra.backend.thesis;

import cloudflight.integra.backend.thesis.model.Thesis;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ThesisRepository {

    private final Map<UUID, Thesis> theses = new HashMap<>();

    public List<Thesis> findAll() {
        return new ArrayList<>(theses.values());
    }

    public Optional<Thesis> findById(UUID id) {
        return Optional.ofNullable(theses.get(id));
    }

    public Optional<Thesis> findByStudentId(UUID studentId) {
        return theses.values()
            .stream()
            .filter(thesis -> thesis.getStudentId().equals(studentId))
            .findFirst();
    }

    public Thesis save(Thesis thesis) {
        if (thesis.getId() == null) {
            thesis.setId(UUID.randomUUID());
        }

        theses.put(thesis.getId(), thesis);
        return thesis;
    }

    public void deleteById(UUID id) {
        theses.remove(id);
    }
}

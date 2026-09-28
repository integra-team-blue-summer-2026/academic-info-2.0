package cloudflight.integra.backend.graduationthesis;

import cloudflight.integra.backend.graduationthesis.model.GraduationThesis;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GraduationThesisRepository {

    private final Map<UUID, GraduationThesis> theses = new HashMap<>();

    public GraduationThesis save(GraduationThesis thesis) {
        theses.put(thesis.getId(), thesis);
        return thesis;
    }

    public Optional<GraduationThesis> findById(UUID id) {
        return Optional.ofNullable(theses.get(id));
    }

    public List<GraduationThesis> findAll() {
        return new ArrayList<>(theses.values());
    }

    public List<GraduationThesis> findByAdvisorId(UUID advisorId) {
        return theses.values()
            .stream()
            .filter(thesis -> advisorId.equals(thesis.getAdvisorId()))
            .toList();
    }
}

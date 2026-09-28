package cloudflight.integra.backend.graduationthesis;

import cloudflight.integra.backend.graduationthesis.model.GraduationThesis;
import cloudflight.integra.backend.graduationthesis.model.ThesisStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GraduationThesisService {

    private final GraduationThesisRepository repository;

    public GraduationThesisService(GraduationThesisRepository repository) {
        this.repository = repository;
    }

    public List<GraduationThesis> getByAdvisorId(UUID advisorId) {
        return repository.findByAdvisorId(advisorId);
    }

    public Optional<GraduationThesis> getById(UUID id) {
        return repository.findById(id);
    }

    public GraduationThesis create(GraduationThesis thesis) {
        thesis.setId(UUID.randomUUID());
        thesis.setStatus(ThesisStatus.UNCHECKED);
        thesis.setRejectionMessage(null);

        return repository.save(thesis);
    }

    public Optional<GraduationThesis> admit(UUID id) {
        return repository.findById(id)
            .map(thesis -> {
                thesis.setStatus(ThesisStatus.CHECKED);
                thesis.setRejectionMessage(null);
                return repository.save(thesis);
            });
    }

    public Optional<GraduationThesis> reject(UUID id, String message) {
        return repository.findById(id)
            .map(thesis -> {
                thesis.setStatus(ThesisStatus.REJECTED);
                thesis.setRejectionMessage(message);
                return repository.save(thesis);
            });
    }
}

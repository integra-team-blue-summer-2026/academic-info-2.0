package cloudflight.integra.backend.examregistration;

import cloudflight.integra.backend.examregistration.model.ExamRegistration;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ExamRegistrationRepository {
    private final Map<UUID, ExamRegistration> registrations = new HashMap<>();

    public List<ExamRegistration> findAll() {
        return new ArrayList<>(registrations.values());
    }

    public Optional<ExamRegistration> findById(UUID id) {
        return Optional.ofNullable(registrations.get(id));
    }

    public ExamRegistration save(ExamRegistration registration) {
        if (registration.getId() == null) {
            registration.setId(UUID.randomUUID());
        }
        registrations.put(registration.getId(), registration);
        return registration;
    }

    public void deleteById(UUID id) {
        registrations.remove(id);
    }

    public List<ExamRegistration> findByStudentId(UUID studentId) {
        return registrations.values().stream()
            .filter(r -> studentId.equals(r.getStudentId())).toList();
    }

    public Optional<ExamRegistration> findByStudentIdAndExamId(UUID studentId, UUID examId) {
        return registrations.values().stream()
            .filter(r -> studentId.equals(r.getStudentId()) && examId.equals(r.getExamId()))
            .findFirst();
    }
}

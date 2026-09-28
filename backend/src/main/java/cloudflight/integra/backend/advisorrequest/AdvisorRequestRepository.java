package cloudflight.integra.backend.advisorrequest;

import cloudflight.integra.backend.advisorrequest.model.AdvisorRequest;
import cloudflight.integra.backend.advisorrequest.model.AdvisorRequestStatus;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AdvisorRequestRepository {
    private final Map<UUID, AdvisorRequest> requests = new HashMap<>();

    public List<AdvisorRequest> findAll() {
        return new ArrayList<>(requests.values());
    }

    public Optional<AdvisorRequest> findById(UUID id) {
        return Optional.ofNullable(requests.get(id));
    }

    public AdvisorRequest save(AdvisorRequest request) {
        if (request.getId() == null) {
            request.setId(UUID.randomUUID());
        }
        requests.put(request.getId(), request);
        return request;
    }

    public void deleteById(UUID id) {
        requests.remove(id);
    }

    public List<AdvisorRequest> findByStudentId(UUID studentId) {
        return requests.values().stream()
            .filter(r -> studentId.equals(r.getStudentId()))
            .toList();
    }

    public List<AdvisorRequest> findByTeacherId(UUID teacherId) {
        return requests.values().stream()
            .filter(r -> teacherId.equals(r.getTeacherId()))
            .toList();
    }

    public long countByStudentIdAndStatus(UUID studentId, AdvisorRequestStatus status) {
        return requests.values().stream()
            .filter(r -> studentId.equals(r.getStudentId()) && r.getStatus() == status)
            .count();
    }

    public Optional<AdvisorRequest> findByStudentIdAndTeacherId(UUID studentId, UUID teacherId) {
        return requests.values().stream()
            .filter(r -> studentId.equals(r.getStudentId()) && teacherId.equals(r.getTeacherId()))
            .findFirst();
    }

}

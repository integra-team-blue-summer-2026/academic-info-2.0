package cloudflight.integra.backend.advisorrequest;

import cloudflight.integra.backend.advisorrequest.model.AdvisorRequest;
import cloudflight.integra.backend.advisorrequest.model.AdvisorRequestStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AdvisorRequestService {
    private static final int MAX_PENDING_REQUESTS = 3;

    private final AdvisorRequestRepository repository;

    public AdvisorRequestService(AdvisorRequestRepository advisorRequestRepository) {
        this.repository = advisorRequestRepository;
    }

    public List<AdvisorRequest> getByStudent(UUID studentId) {
        return repository.findByStudentId(studentId);
    }

    public List<AdvisorRequest> getByTeacher(UUID teacherId) {
        return repository.findByTeacherId(teacherId);
    }

    public AdvisorRequest apply(AdvisorRequest request){
        validate(request);
        request.setStatus(AdvisorRequestStatus.PENDING);
        return repository.save(request);
    }

    public void validate(AdvisorRequest request){
        if(request.getStudentId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student Id is required");
        }
        if(request.getTeacherId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teacher Id is required");
        }

        long approved = repository.countByStudentIdAndStatus(request.getStudentId(), AdvisorRequestStatus.APPROVED);

        if(approved > 0){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have an advisor request");
        }

        boolean alreadyRequested = repository.findByStudentIdAndTeacherId(request.getStudentId(), request.getTeacherId()).isPresent();
        if(alreadyRequested){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already send request");
        }

        long pending = repository.countByStudentIdAndStatus(request.getStudentId(), AdvisorRequestStatus.PENDING);
        if(pending >= MAX_PENDING_REQUESTS){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can have at most " + MAX_PENDING_REQUESTS + " pending requests");
        }

    }

    public AdvisorRequest approve(UUID id){
        AdvisorRequest request = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        request.setStatus(AdvisorRequestStatus.APPROVED);
        repository.save(request);

        repository.findByStudentId(request.getStudentId()).stream()
            .filter(r -> r.getStatus() == AdvisorRequestStatus.PENDING)
            .forEach(r -> {
                r.setStatus(AdvisorRequestStatus.CANCELLED);
                repository.save(r);
            });

        return request;
    }

    public AdvisorRequest reject(UUID id) {
        AdvisorRequest request = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        request.setStatus(AdvisorRequestStatus.REJECTED);
        return repository.save(request);
    }
}

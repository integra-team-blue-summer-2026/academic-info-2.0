package cloudflight.integra.backend.examregistration;

import cloudflight.integra.backend.exam.ExamService;
import cloudflight.integra.backend.exam.model.Exam;
import cloudflight.integra.backend.examregistration.model.ExamRegistration;
import cloudflight.integra.backend.examregistration.model.ExamSlot;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExamRegistrationService {
    private final ExamRegistrationRepository repository;
    private final ExamService examService;

    public ExamRegistrationService(ExamRegistrationRepository repository, ExamService examService) {
        this.repository = repository;
        this.examService = examService;
    }

    public List<ExamRegistration> getAll() {
        return repository.findAll();
    }

    public List<ExamRegistration> getByStudent(UUID studentId) {
        return repository.findByStudentId(studentId);
    }

    public ExamRegistration register(ExamRegistration registration) {
        validate(registration);
        return repository.save(registration);
    }

    public boolean delete(UUID id) {
        return repository.findById(id).map(existing -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }

    private void validate(ExamRegistration registration) {
        if (registration.getStudentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "StudentId is required");
        }
        if (registration.getExamId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ExamId is required");
        }
        if (registration.getChosenSlot() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must choose a date");
        }

        Exam exam = examService.getById(registration.getExamId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));

        if (registration.getChosenSlot() == ExamSlot.SECONDARY && exam.getSecondaryDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This exam has no secondary date");
        }
        if (registration.getChosenSlot() == ExamSlot.PRIMARY && exam.getPrimaryDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This exam has no primary date");
        }

        Optional<ExamRegistration> existing =
            repository.findByStudentIdAndExamId(registration.getStudentId(), registration.getExamId());
        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already registered for this exam");
        }
    }
}

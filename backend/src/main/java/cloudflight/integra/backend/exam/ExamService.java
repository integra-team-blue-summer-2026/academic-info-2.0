package cloudflight.integra.backend.exam;

import cloudflight.integra.backend.exam.model.Exam;
import cloudflight.integra.backend.exam.model.ExamType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExamService {
    private final ExamRepository repository;

    public ExamService(ExamRepository repository) {
        this.repository = repository;
    }

    public List<Exam> getAll() {
        return repository.findAll();
    }

    public Optional<Exam> getById(UUID id) {
        return repository.findById(id);
    }

    public Exam create(Exam exam) {
        validate(exam, null);
        return repository.save(exam);
    }

    public Optional<Exam> update(UUID id, Exam exam) {
        return repository.findById(id).map(existing -> {
            exam.setId(id);
            validate(exam, id);
            return repository.save(exam);
        });
    }

    public boolean delete(UUID id) {
        return repository.findById(id).map(existing -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }

    private void validate(Exam exam, UUID excludeId){
        validateDates(exam);
        validateGroupNotBusy(exam, excludeId);
        validateRoomAvailable(exam, excludeId);
    }

    private void validateDates(Exam exam) {
        if(exam.getCourseId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CourseId is required");
        }
        if(exam.getPrimaryDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PrimaryDate is required");
        }
        if(exam.getExamType() == ExamType.FINAL && exam.getSecondaryDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SecondaryDate is required");
        }
    }

    private void validateGroupNotBusy(Exam exam, UUID excludeId) {
        List<Exam> sameGroup = repository.findByGroup(exam.getGroup());
        for (Exam existing : sameGroup) {
            if (existing.getId().equals(excludeId)) {
                continue;
            }
            for (LocalDateTime newDate : datesOf(exam)) {
                for (LocalDateTime existingDate : datesOf(existing)) {
                    if (sameDay(newDate, existingDate)) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "This group already has an exam on that day.");
                    }
                }
            }
        }
    }

    private List<LocalDateTime> datesOf(Exam exam) {
        List<LocalDateTime> dates = new ArrayList<>();
        if (exam.getPrimaryDate() != null) {
            dates.add(exam.getPrimaryDate());
        }
        if (exam.getSecondaryDate() != null) {
            dates.add(exam.getSecondaryDate());
        }
        return dates;
    }

    private boolean sameDay(LocalDateTime d1, LocalDateTime d2) {
        if(d1 == null || d2 == null) {
            return false;
        }
        return d1.toLocalDate().equals(d2.toLocalDate());
    }

    private void validateRoomAvailable(Exam exam, UUID excludeId) {
        List<Exam> sameRoom = repository.findByRoom(exam.getRoom());
        for (Exam existing : sameRoom) {
            if (existing.getId().equals(excludeId)) {
                continue;
            }
            if (existing.getCourseId().equals(exam.getCourseId())) {
                continue;
            }
            for (LocalDateTime newDate : datesOf(exam)) {
                for (LocalDateTime existingDate : datesOf(existing)) {
                    if (sameDateTime(newDate, existingDate)) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "The room is already booked at that time.");
                    }
                }
            }
        }
    }

    private boolean sameDateTime(LocalDateTime d1, LocalDateTime d2) {
        if(d1 == null || d2 == null) {
            return false;
        }
        return d1.equals(d2);
    }

}

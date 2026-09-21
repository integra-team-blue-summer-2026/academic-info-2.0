package cloudflight.integra.backend.examregistration;

import static org.junit.jupiter.api.Assertions.*;

import cloudflight.integra.backend.exam.ExamService;
import cloudflight.integra.backend.exam.model.Exam;
import cloudflight.integra.backend.exam.model.ExamType;
import cloudflight.integra.backend.examregistration.model.ExamRegistration;
import cloudflight.integra.backend.examregistration.model.ExamSlot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ExamRegistrationServiceTest {

    @Autowired
    private ExamRegistrationService service;

    @Autowired
    private ExamService examService;

    private Exam createExam(boolean withSecondaryDate) {
        Exam exam = new Exam();
        exam.setExamType(ExamType.PARTIAL);
        exam.setGroup("221");
        exam.setRoom("C310");
        exam.setCourseId(UUID.randomUUID());
        exam.setPrimaryDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        if (withSecondaryDate) {
            exam.setSecondaryDate(LocalDateTime.of(2026, 6, 20, 10, 0));
        }
        return examService.create(exam);
    }

    @Test
    void shouldCreateValidRegistration() {
        Exam exam = createExam(false);

        ExamRegistration registration = new ExamRegistration();
        registration.setStudentId(UUID.randomUUID());
        registration.setExamId(exam.getId());
        registration.setChosenSlot(ExamSlot.PRIMARY);

        assertDoesNotThrow(() -> service.register(registration));
    }

    @Test
    void shouldRejectRegistrationForNonexistentExam() {
        ExamRegistration registration = new ExamRegistration();
        registration.setStudentId(UUID.randomUUID());
        registration.setExamId(UUID.randomUUID()); // examen care nu exista
        registration.setChosenSlot(ExamSlot.PRIMARY);

        assertThrows(ResponseStatusException.class, () -> service.register(registration));
    }

    @Test
    void shouldRejectSecondarySlotWhenExamHasNoSecondaryDate() {
        Exam exam = createExam(false); // examen fara data secundara

        ExamRegistration registration = new ExamRegistration();
        registration.setStudentId(UUID.randomUUID());
        registration.setExamId(exam.getId());
        registration.setChosenSlot(ExamSlot.SECONDARY); // alege un slot inexistent

        assertThrows(ResponseStatusException.class, () -> service.register(registration));
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        Exam exam = createExam(true);
        UUID studentId = UUID.randomUUID();

        ExamRegistration first = new ExamRegistration();
        first.setStudentId(studentId);
        first.setExamId(exam.getId());
        first.setChosenSlot(ExamSlot.PRIMARY);
        service.register(first);

        ExamRegistration second = new ExamRegistration();
        second.setStudentId(studentId);
        second.setExamId(exam.getId());
        second.setChosenSlot(ExamSlot.SECONDARY);

        assertThrows(ResponseStatusException.class, () -> service.register(second));
    }
}

package cloudflight.integra.backend.exam;
import cloudflight.integra.backend.exam.model.Exam;
import cloudflight.integra.backend.exam.model.ExamType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ExamServiceTest {
    @Autowired
    private ExamService service;


    @Test
    void testCreateAndGetById() {
        Exam exam = new Exam();
        UUID examId = UUID.randomUUID();
        exam.setId(examId);
        exam.setExamType(ExamType.PARTIAL);
        exam.setRoom("C310");
        exam.setPrimaryDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        exam.setCourseId(UUID.randomUUID());

        service.create(exam);

        Optional<Exam> found = service.getById(examId);
        assertTrue(found.isPresent());
        assertEquals(ExamType.PARTIAL, found.get().getExamType());
        assertEquals("C310", found.get().getRoom());
    }

    @Test
    void testGetAll() {
        Exam exam1 = new Exam();
        exam1.setId(UUID.randomUUID());
        exam1.setPrimaryDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        exam1.setRoom("C310");
        exam1.setGroup("221");
        exam1.setCourseId(UUID.randomUUID());
        Exam exam2 = new Exam();
        exam2.setId(UUID.randomUUID());
        exam2.setCourseId(UUID.randomUUID());
        exam2.setPrimaryDate(LocalDateTime.of(2026, 6, 16, 10, 0));
        exam2.setRoom("C311");
        exam2.setGroup("222");

        service.create(exam1);
        service.create(exam2);

        List<Exam> allExams = service.getAll();

        assertEquals(2, allExams.size());
        assertTrue(allExams.stream().anyMatch(e -> e.getId().equals(exam1.getId())));
        assertTrue(allExams.stream().anyMatch(e -> e.getId().equals(exam2.getId())));
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        Exam exam = new Exam();
        exam.setId(id);
        exam.setPrimaryDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        exam.setCourseId(UUID.randomUUID());

        service.create(exam);

        boolean isDeleted = service.delete(id);

        assertTrue(isDeleted);
        assertTrue(service.getById(id).isEmpty());
    }

    @Test
    void shouldRejectSecondExamSameDay() {
        Exam first = new Exam();
        first.setGroup("221");
        first.setRoom("C310");
        first.setPrimaryDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        first.setCourseId(UUID.randomUUID());
        service.create(first);

        Exam second = new Exam();
        second.setGroup("221");
        second.setRoom("C311");
        second.setPrimaryDate(LocalDateTime.of(2026, 6, 15, 14, 0));
        second.setCourseId(UUID.randomUUID());

        assertThrows(ResponseStatusException.class, () -> service.create(second));
    }

    @Test
    void shouldRejectSameRoomSameTime() {
        LocalDateTime when = LocalDateTime.of(2026, 6, 15, 10, 0);

        Exam first = new Exam();
        first.setGroup("221");
        first.setRoom("C310");
        first.setPrimaryDate(when);
        first.setCourseId(UUID.randomUUID());
        service.create(first);

        Exam second = new Exam();
        second.setGroup("222");
        second.setRoom("C310");
        second.setPrimaryDate(when);
        second.setCourseId(UUID.randomUUID());

        assertThrows(ResponseStatusException.class, () -> service.create(second));
    }

    @Test
    void shouldRejectFinalWithoutSecondDate() {
        Exam exam = new Exam();
        exam.setExamType(ExamType.FINAL);
        exam.setGroup("221");
        exam.setRoom("C310");
        exam.setPrimaryDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        exam.setCourseId(UUID.randomUUID());

        assertThrows(ResponseStatusException.class, () -> service.create(exam));
    }

    @Test
    void shouldRejectMissingPrimaryDate() {
        Exam exam = new Exam();
        exam.setExamType(ExamType.PARTIAL);
        exam.setGroup("221");
        exam.setRoom("C310");
        exam.setCourseId(UUID.randomUUID());

        assertThrows(ResponseStatusException.class, () -> service.create(exam));
    }

    @Test
    void shouldCreateValidExam() {
        Exam exam = new Exam();
        exam.setExamType(ExamType.PARTIAL);
        exam.setGroup("221");
        exam.setRoom("C310");
        exam.setPrimaryDate(LocalDateTime.of(2026, 6, 15, 10, 0));
        exam.setCourseId(UUID.randomUUID());

        assertDoesNotThrow(() -> service.create(exam));
    }

    @Test
    void shouldAllowSameRoomSameTimeForCoupledGroups() {
        UUID courseId = UUID.randomUUID();
        LocalDateTime when = LocalDateTime.of(2026, 6, 15, 10, 0);

        Exam first = new Exam();
        first.setCourseId(courseId);
        first.setGroup("221");
        first.setRoom("C310");
        first.setPrimaryDate(when);
        service.create(first);

        Exam second = new Exam();
        second.setCourseId(courseId);
        second.setGroup("222");
        second.setRoom("C310");
        second.setPrimaryDate(when);

        assertDoesNotThrow(() -> service.create(second));
    }
}

package cloudflight.integra.backend.exam;
import cloudflight.integra.backend.exam.model.Exam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        exam.setExamType("Partial");
        exam.setRoom("C310");

        service.create(exam);

        Optional<Exam> found = service.getById(examId);
        assertTrue(found.isPresent());
        assertEquals("Partial", found.get().getExamType());
        assertEquals("C310", found.get().getRoom());
    }

    @Test
    void testGetAll() {
        Exam exam1 = new Exam();
        exam1.setId(UUID.randomUUID());
        Exam exam2 = new Exam();
        exam2.setId(UUID.randomUUID());

        service.create(exam1);
        service.create(exam2);

        List<Exam> allExams = service.getAll();

        assertEquals(2, allExams.size());
        assertEquals(exam1.getId(), allExams.get(0).getId());
        assertEquals(exam2.getId(), allExams.get(1).getId());
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        Exam exam = new Exam();
        exam.setId(id);

        service.create(exam);

        boolean isDeleted = service.delete(id);

        assertTrue(isDeleted);
        assertTrue(service.getById(id).isEmpty());
    }
}

package cloudflight.integra.backend;

import cloudflight.integra.backend.graduationthesis.GraduationThesisRepository;
import cloudflight.integra.backend.graduationthesis.model.GraduationThesis;
import cloudflight.integra.backend.graduationthesis.model.ThesisStatus;
import cloudflight.integra.backend.student.StudentRepository;
import cloudflight.integra.backend.student.model.Student;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TestDataInitializer {

    private static final UUID ADVISOR_ID =
        UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final StudentRepository studentRepository;
    private final GraduationThesisRepository thesisRepository;

    public TestDataInitializer(
        StudentRepository studentRepository,
        GraduationThesisRepository thesisRepository
    ) {
        this.studentRepository = studentRepository;
        this.thesisRepository = thesisRepository;
    }

    @PostConstruct
    public void initialize() {
        Student firstStudent = new Student();
        firstStudent.setId(
            UUID.fromString("22222222-2222-2222-2222-222222222222")
        );
        firstStudent.setNationalId("TEST001");
        firstStudent.setFirstName("Emma");
        firstStudent.setLastName("Johnson");
        firstStudent.setDateOfBirth("2002-05-14");
        firstStudent.setEmail("emma.johnson@example.com");
        firstStudent.setGroup("A1");

        studentRepository.save(firstStudent);

        Student secondStudent = new Student();
        secondStudent.setId(
            UUID.fromString("33333333-3333-3333-3333-333333333333")
        );
        secondStudent.setNationalId("TEST002");
        secondStudent.setFirstName("Daniel");
        secondStudent.setLastName("Smith");
        secondStudent.setDateOfBirth("2001-11-08");
        secondStudent.setEmail("daniel.smith@example.com");
        secondStudent.setGroup("A1");

        studentRepository.save(secondStudent);

        GraduationThesis firstThesis = new GraduationThesis();
        firstThesis.setId(
            UUID.fromString("44444444-4444-4444-4444-444444444444")
        );
        firstThesis.setStudentId(firstStudent.getId());
        firstThesis.setAdvisorId(ADVISOR_ID);
        firstThesis.setTitle("Artificial Intelligence in Education");
        firstThesis.setFileName("artificial-intelligence-thesis.pdf");
        firstThesis.setStatus(ThesisStatus.UNCHECKED);
        firstThesis.setRejectionMessage(null);

        thesisRepository.save(firstThesis);

        GraduationThesis secondThesis = new GraduationThesis();
        secondThesis.setId(
            UUID.fromString("55555555-5555-5555-5555-555555555555")
        );
        secondThesis.setStudentId(secondStudent.getId());
        secondThesis.setAdvisorId(ADVISOR_ID);
        secondThesis.setTitle("Cloud Computing for Academic Systems");
        secondThesis.setFileName("cloud-computing-thesis.pdf");
        secondThesis.setStatus(ThesisStatus.UNCHECKED);
        secondThesis.setRejectionMessage(null);

        thesisRepository.save(secondThesis);
    }
}

package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TeacherRepositoryTest {

    private TeacherRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TeacherRepository();
    }

    @Test
    void shouldSaveTeacherAndGenerateId() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher savedTeacher = repository.save(teacher);

        assertNotNull(savedTeacher.getId());
        assertEquals("Ana", savedTeacher.getFirstName());
        assertEquals("Popescu", savedTeacher.getLastName());
        assertEquals("Assoc. Prof.", savedTeacher.getTitle());
        assertEquals("Computer Science", savedTeacher.getDepartment());
    }

    @Test
    void shouldFindTeacherById() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher savedTeacher = repository.save(teacher);

        Optional<Teacher> result = repository.findById(savedTeacher.getId());

        assertTrue(result.isPresent());
        assertEquals(savedTeacher.getId(), result.get().getId());
        assertEquals("Ana", result.get().getFirstName());
        assertEquals("Popescu", result.get().getLastName());
    }

    @Test
    void shouldReturnEmptyWhenTeacherDoesNotExist() {
        UUID unknownId = UUID.randomUUID();

        Optional<Teacher> result = repository.findById(unknownId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllTeachers() {
        Teacher firstTeacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher secondTeacher = new Teacher(
            null,
            "Mihai",
            "Ionescu",
            "PhD",
            "Mathematics"
        );

        repository.save(firstTeacher);
        repository.save(secondTeacher);

        List<Teacher> teachers = repository.findAll();

        assertEquals(2, teachers.size());
        assertTrue(teachers.contains(firstTeacher));
        assertTrue(teachers.contains(secondTeacher));
    }

    @Test
    void shouldKeepExistingIdWhenSavingTeacher() {
        UUID id = UUID.randomUUID();

        Teacher teacher = new Teacher(
            id,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher savedTeacher = repository.save(teacher);

        assertEquals(id, savedTeacher.getId());
        assertTrue(repository.findById(id).isPresent());
    }

    @Test
    void shouldUpdateExistingTeacher() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher savedTeacher = repository.save(teacher);

        Teacher updatedTeacherData = new Teacher(
            savedTeacher.getId(),
            "Ana",
            "Popescu",
            "Prof.",
            "Software Engineering"
        );

        Teacher updatedTeacher = repository.save(updatedTeacherData);

        assertEquals(savedTeacher.getId(), updatedTeacher.getId());
        assertEquals("Prof.", updatedTeacher.getTitle());
        assertEquals("Software Engineering", updatedTeacher.getDepartment());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldDeleteTeacherById() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher savedTeacher = repository.save(teacher);

        repository.deleteById(savedTeacher.getId());

        assertTrue(repository.findById(savedTeacher.getId()).isEmpty());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldDoNothingWhenDeletingUnknownId() {
        UUID unknownId = UUID.randomUUID();

        repository.deleteById(unknownId);

        assertTrue(repository.findAll().isEmpty());
    }
}

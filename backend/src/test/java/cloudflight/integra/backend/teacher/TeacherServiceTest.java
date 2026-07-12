package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TeacherServiceTest {

    private TeacherRepository repository;
    private TeacherService service;

    @BeforeEach
    void setUp() {
        repository = new TeacherRepository();
        service = new TeacherService(repository);
    }

    @Test
    void shouldReturnAllTeachers() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        repository.save(teacher);

        List<Teacher> teachers = service.getAll();

        assertEquals(1, teachers.size());
        assertEquals("Ana", teachers.getFirst().getFirstName());
    }

    @Test
    void shouldReturnTeacherById() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher savedTeacher = repository.save(teacher);

        Optional<Teacher> result = service.getById(savedTeacher.getId());

        assertTrue(result.isPresent());
        assertEquals(savedTeacher.getId(), result.get().getId());
    }

    @Test
    void shouldCreateTeacher() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher createdTeacher = service.create(teacher);

        assertNotNull(createdTeacher.getId());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldUpdateTeacher() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher savedTeacher = repository.save(teacher);

        Teacher updatedTeacher = new Teacher(
            savedTeacher.getId(),
            "Ana",
            "Popescu",
            "Prof.",
            "Software Engineering"
        );

        Optional<Teacher> result = service.update(savedTeacher.getId(), updatedTeacher);

        assertTrue(result.isPresent());
        assertEquals("Prof.", result.get().getTitle());
        assertEquals("Software Engineering", result.get().getDepartment());
    }

    @Test
    void shouldReturnEmptyWhenUpdatingUnknownTeacher() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Prof.",
            "Computer Science"
        );

        Optional<Teacher> result = service.update(UUID.randomUUID(), teacher);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteTeacher() {
        Teacher teacher = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher savedTeacher = repository.save(teacher);

        boolean deleted = service.delete(savedTeacher.getId());

        assertTrue(deleted);
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void shouldReturnFalseWhenDeletingUnknownTeacher() {
        boolean deleted = service.delete(UUID.randomUUID());

        assertFalse(deleted);
    }
}

package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TeacherServiceTest {
    @Autowired
    private TeacherService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Teacher newTeacher() {
        return new Teacher(null, "Ana", "Popescu", "Assoc. Prof.", "Computer Science");
    }

    @Test
    void shouldStoreAHashedPasswordWhenCreatingATeacher() {
        Teacher created = service.create(newTeacher());

        assertNotNull(created.getPasswordHash());
        assertTrue(created.getPasswordHash().startsWith("$2"));
    }

    @Test
    void shouldGenerateDifferentPasswordsForDifferentTeachers() {
        Teacher first = service.create(newTeacher());
        Teacher second = service.create(newTeacher());

        assertNotEquals(first.getPasswordHash(), second.getPasswordHash());
    }

    @Test
    void shouldKeepThePasswordHashWhenUpdatingATeacher() {
        Teacher created = service.create(newTeacher());
        String originalHash = created.getPasswordHash();

        Teacher updatePayload = new Teacher(null, "Ana", "Ionescu", "Prof.", "Software Engineering");
        Optional<Teacher> updated = service.update(created.getId(), updatePayload);

        assertTrue(updated.isPresent());
        assertEquals("Ionescu", updated.get().getLastName());
        assertEquals(originalHash, updated.get().getPasswordHash());
    }

    @Test
    void shouldReturnThePlainPasswordOnlyWhenRegenerating() {
        Teacher created = service.create(newTeacher());

        Optional<String> plainPassword = service.regeneratePassword(created.getId());

        assertTrue(plainPassword.isPresent());

        Optional<Teacher> reloaded = service.getById(created.getId());
        assertTrue(reloaded.isPresent());
        assertTrue(passwordEncoder.matches(plainPassword.get(), reloaded.get().getPasswordHash()));
    }

    @Test
    void shouldChangeThePasswordHashWhenRegenerating() {
        Teacher created = service.create(newTeacher());
        String originalHash = created.getPasswordHash();

        service.regeneratePassword(created.getId());

        Optional<Teacher> reloaded = service.getById(created.getId());
        assertTrue(reloaded.isPresent());
        assertNotEquals(originalHash, reloaded.get().getPasswordHash());
    }

    @Test
    void shouldNotRegeneratePasswordForUnknownTeacher() {
        Optional<String> plainPassword = service.regeneratePassword(UUID.randomUUID());

        assertTrue(plainPassword.isEmpty());
    }

}

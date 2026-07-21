package cloudflight.integra.backend.teacher;

import cloudflight.integra.backend.teacher.model.Teacher;
import cloudflight.integra.backend.teacher.model.TeacherDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.verify;

@WebMvcTest(TeacherController.class)
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeacherService service;

    @MockitoBean
    private TeacherMapper mapper;

    @Test
    void shouldReturnAllTeachers() throws Exception {

        UUID id = UUID.randomUUID();

        Teacher teacher = new Teacher(
            id,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        TeacherDto dto = new TeacherDto(
            id,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        when(service.getAll()).thenReturn(List.of(teacher));
        when(mapper.toDto(teacher)).thenReturn(dto);

        mockMvc.perform(get("/api/teachers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].firstName").value("Ana"))
            .andExpect(jsonPath("$[0].lastName").value("Popescu"));
    }

    @Test
    void shouldReturnTeacherById() throws Exception {

        UUID id = UUID.randomUUID();

        Teacher teacher = new Teacher(
            id,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        TeacherDto dto = new TeacherDto(
            id,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        when(service.getById(id)).thenReturn(Optional.of(teacher));
        when(mapper.toDto(teacher)).thenReturn(dto);

        mockMvc.perform(get("/api/teachers/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    void shouldReturn404WhenTeacherDoesNotExist() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.getById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/teachers/" + id))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateTeacher() throws Exception {

        UUID id = UUID.randomUUID();

        Teacher teacher = new Teacher(
            id,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        TeacherDto dto = new TeacherDto(
            id,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        when(mapper.toEntity(any())).thenReturn(teacher);
        when(service.create(any())).thenReturn(teacher);
        when(mapper.toDto(teacher)).thenReturn(dto);

        mockMvc.perform(post("/api/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName":"Ana",
                          "lastName":"Popescu",
                          "title":"Assoc. Prof.",
                          "department":"Computer Science"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    void shouldUpdateTeacher() throws Exception {

        UUID id = UUID.randomUUID();

        Teacher teacher = new Teacher(
            id,
            "Ana",
            "Popescu",
            "Prof.",
            "Software Engineering"
        );

        TeacherDto dto = new TeacherDto(
            id,
            "Ana",
            "Popescu",
            "Prof.",
            "Software Engineering"
        );

        when(mapper.toEntity(any())).thenReturn(teacher);
        when(service.update(eq(id), any())).thenReturn(Optional.of(teacher));
        when(mapper.toDto(teacher)).thenReturn(dto);

        mockMvc.perform(put("/api/teachers/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName":"Ana",
                          "lastName":"Popescu",
                          "title":"Prof.",
                          "department":"Software Engineering"
                        }
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Prof."));
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownTeacher() throws Exception {

        UUID id = UUID.randomUUID();

        when(mapper.toEntity(any())).thenReturn(new Teacher());
        when(service.update(eq(id), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/teachers/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "firstName":"Ana"
                        }
                        """))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteTeacher() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.delete(id)).thenReturn(true);

        mockMvc.perform(delete("/api/teachers/" + id))
            .andExpect(status().isNoContent());

        verify(service).delete(id);
    }

    @Test
    void shouldReturn404WhenDeletingUnknownTeacher() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.delete(id)).thenReturn(false);

        mockMvc.perform(delete("/api/teachers/" + id))
            .andExpect(status().isNotFound());

        verify(service).delete(id);
    }

    @Test
    void shouldIgnoreClientProvidedIdWhenCreatingTeacher() throws Exception {

        UUID clientProvidedId = UUID.randomUUID();
        UUID generatedId = UUID.randomUUID();

        Teacher teacherWithoutClientId = new Teacher(
            null,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        Teacher savedTeacher = new Teacher(
            generatedId,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        TeacherDto responseDto = new TeacherDto(
            generatedId,
            "Ana",
            "Popescu",
            "Assoc. Prof.",
            "Computer Science"
        );

        when(mapper.toEntity(any())).thenReturn(teacherWithoutClientId);
        when(service.create(teacherWithoutClientId)).thenReturn(savedTeacher);
        when(mapper.toDto(savedTeacher)).thenReturn(responseDto);

        mockMvc.perform(post("/api/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id":"%s",
                      "firstName":"Ana",
                      "lastName":"Popescu",
                      "title":"Assoc. Prof.",
                      "department":"Computer Science"
                    }
                    """.formatted(clientProvidedId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(generatedId.toString()));

        verify(mapper).toEntity(
            new TeacherDto(
                null,
                "Ana",
                "Popescu",
                "Assoc. Prof.",
                "Computer Science"
            )
        );
    }

    @Test
    void shouldReturn400WhenCreateBodyIsNull() throws Exception {

        mockMvc.perform(post("/api/teachers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdateBodyIsNull() throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/teachers/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
            .andExpect(status().isBadRequest());
    }
}

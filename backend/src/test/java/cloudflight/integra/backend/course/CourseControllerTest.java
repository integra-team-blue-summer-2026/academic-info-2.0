package cloudflight.integra.backend.course;

import cloudflight.integra.backend.course.model.Course;
import cloudflight.integra.backend.course.model.CourseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService service;

    @MockitoBean
    private CourseMapper mapper;

    @Test
    void shouldReturnAllCourses() throws Exception {

        UUID id = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        Course course = new Course(id, teacherId,
            "Databases", "db.pdf", 6, "Database course");

        CourseDto dto = new CourseDto(id, teacherId,
            "Databases", "db.pdf", 6, "Database course");

        when(service.getAll()).thenReturn(List.of(course));
        when(mapper.toDto(course)).thenReturn(dto);

        mockMvc.perform(get("/api/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].courseName").value("Databases"))
            .andExpect(jsonPath("$[0].credits").value(6));
    }

    @Test
    void shouldReturnCourseById() throws Exception {

        UUID id = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        Course course = new Course(id, teacherId,
            "Algorithms", "alg.pdf", 5, "Algorithms");

        CourseDto dto = new CourseDto(id, teacherId,
            "Algorithms", "alg.pdf", 5, "Algorithms");

        when(service.getById(id)).thenReturn(Optional.of(course));
        when(mapper.toDto(course)).thenReturn(dto);

        mockMvc.perform(get("/api/courses/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courseName").value("Algorithms"))
            .andExpect(jsonPath("$.credits").value(5));
    }

    @Test
    void shouldReturn404WhenCourseNotFound() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.getById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/courses/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateCourse() throws Exception {

        UUID id = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        Course course = new Course(id, teacherId,
            "Networks", "net.pdf", 4, "Networks");

        CourseDto dto = new CourseDto(id, teacherId,
            "Networks", "net.pdf", 4, "Networks");

        when(mapper.toEntity(any())).thenReturn(course);
        when(service.create(course)).thenReturn(course);
        when(mapper.toDto(course)).thenReturn(dto);

        String json = """
                {
                  "teacherId":"%s",
                  "courseName":"Networks",
                  "syllabus":"net.pdf",
                  "credits":4,
                  "description":"Networks"
                }
                """.formatted(teacherId);

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.courseName").value("Networks"))
            .andExpect(jsonPath("$.credits").value(4));
    }

    @Test
    void shouldUpdateCourse() throws Exception {

        UUID id = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        Course course = new Course(id, teacherId,
            "Java", "java.pdf", 5, "Java");

        CourseDto dto = new CourseDto(id, teacherId,
            "Java", "java.pdf", 5, "Java");

        when(mapper.toEntity(any())).thenReturn(course);
        when(service.update(eq(id), any())).thenReturn(Optional.of(course));
        when(mapper.toDto(course)).thenReturn(dto);

        String json = """
                {
                  "teacherId":"%s",
                  "courseName":"Java",
                  "syllabus":"java.pdf",
                  "credits":5,
                  "description":"Java"
                }
                """.formatted(teacherId);

        mockMvc.perform(put("/api/courses/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.courseName").value("Java"));
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownCourse() throws Exception {

        UUID id = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        Course course = new Course();

        when(mapper.toEntity(any())).thenReturn(course);
        when(service.update(eq(id), any())).thenReturn(Optional.empty());

        String json = """
                {
                  "teacherId":"%s",
                  "courseName":"Java",
                  "syllabus":"java.pdf",
                  "credits":5,
                  "description":"Java"
                }
                """.formatted(teacherId);

        mockMvc.perform(put("/api/courses/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteCourse() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.delete(id)).thenReturn(true);

        mockMvc.perform(delete("/api/courses/{id}", id))
            .andExpect(status().isOk());

        verify(service).delete(id);
    }

    @Test
    void shouldReturn404WhenDeletingUnknownCourse() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.delete(id)).thenReturn(false);

        mockMvc.perform(delete("/api/courses/{id}", id))
            .andExpect(status().isNotFound());

        verify(service).delete(id);
    }
}

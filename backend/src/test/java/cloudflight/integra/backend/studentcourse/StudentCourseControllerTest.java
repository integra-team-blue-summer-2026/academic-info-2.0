package cloudflight.integra.backend.studentcourse;

import cloudflight.integra.backend.studentcourse.model.StudentCourse;
import cloudflight.integra.backend.studentcourse.model.StudentCourseDto;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentCourseController.class)
class StudentCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentCourseService service;

    @MockitoBean
    private StudentCourseMapper mapper;

    @Test
    void shouldReturnAllStudentCourses() throws Exception {

        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        StudentCourse studentCourse =
            new StudentCourse(id, studentId, courseId);

        StudentCourseDto dto =
            new StudentCourseDto(id, studentId, courseId);

        when(service.getAll()).thenReturn(List.of(studentCourse));
        when(mapper.toDto(studentCourse)).thenReturn(dto);

        mockMvc.perform(get("/api/student-courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(id.toString()))
            .andExpect(jsonPath("$[0].studentId").value(studentId.toString()))
            .andExpect(jsonPath("$[0].courseId").value(courseId.toString()));
    }

    @Test
    void shouldReturnStudentCourseById() throws Exception {

        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        StudentCourse studentCourse =
            new StudentCourse(id, studentId, courseId);

        StudentCourseDto dto =
            new StudentCourseDto(id, studentId, courseId);

        when(service.getById(id))
            .thenReturn(Optional.of(studentCourse));

        when(mapper.toDto(studentCourse))
            .thenReturn(dto);

        mockMvc.perform(get("/api/student-courses/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.studentId").value(studentId.toString()))
            .andExpect(jsonPath("$.courseId").value(courseId.toString()));
    }

    @Test
    void shouldReturn404WhenStudentCourseNotFound() throws Exception {

        UUID id = UUID.randomUUID();

        when(service.getById(id))
            .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/student-courses/{id}", id))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnStudentCoursesByCourseId() throws Exception {

        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        StudentCourse studentCourse =
            new StudentCourse(id, studentId, courseId);

        StudentCourseDto dto =
            new StudentCourseDto(id, studentId, courseId);

        when(service.getByCourseId(courseId))
            .thenReturn(List.of(studentCourse));

        when(mapper.toDto(studentCourse))
            .thenReturn(dto);

        mockMvc.perform(
                get("/api/student-courses/course/{courseId}", courseId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(id.toString()))
            .andExpect(jsonPath("$[0].studentId")
                .value(studentId.toString()))
            .andExpect(jsonPath("$[0].courseId")
                .value(courseId.toString()));
    }

    @Test
    void shouldCreateStudentCourse() throws Exception {

        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        StudentCourse studentCourse =
            new StudentCourse(id, studentId, courseId);

        StudentCourseDto dto =
            new StudentCourseDto(id, studentId, courseId);

        when(mapper.toEntity(any()))
            .thenReturn(studentCourse);

        when(service.create(studentCourse))
            .thenReturn(studentCourse);

        when(mapper.toDto(studentCourse))
            .thenReturn(dto);

        String json = """
                {
                  "id":"%s",
                  "studentId":"%s",
                  "courseId":"%s"
                }
                """.formatted(id, studentId, courseId);

        mockMvc.perform(post("/api/student-courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.studentId")
                .value(studentId.toString()))
            .andExpect(jsonPath("$.courseId")
                .value(courseId.toString()));
    }

    @Test
    void shouldDeleteStudentCourse() throws Exception {

        UUID courseId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        when(service.delete(courseId, studentId))
            .thenReturn(true);

        mockMvc.perform(
                delete(
                    "/api/student-courses/{courseId}/{studentId}",
                    courseId,
                    studentId
                )
            )
            .andExpect(status().isOk());

        verify(service).delete(courseId, studentId);
    }

    @Test
    void shouldReturn404WhenDeletingUnknownStudentCourse()
        throws Exception {

        UUID courseId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        when(service.delete(courseId, studentId))
            .thenReturn(false);

        mockMvc.perform(
                delete(
                    "/api/student-courses/{courseId}/{studentId}",
                    courseId,
                    studentId
                )
            )
            .andExpect(status().isNotFound());

        verify(service).delete(courseId, studentId);
    }
}

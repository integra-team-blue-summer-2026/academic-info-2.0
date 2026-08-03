package cloudflight.integra.backend.studentexam;

import cloudflight.integra.backend.studentexam.model.GradeStatus;
import cloudflight.integra.backend.studentexam.model.StudentExam;
import cloudflight.integra.backend.studentexam.model.StudentExamDto;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentExamController.class)
class StudentExamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentExamService studentExamService;

    @MockitoBean
    private StudentExamMapper studentExamMapper;

    @Test
    void shouldReturnAllStudentExams() throws Exception {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        StudentExam studentExam = new StudentExam(id, studentId, examId, "9", "10", GradeStatus.PASSED);
        StudentExamDto dto = new StudentExamDto(id, studentId, examId, "9", "10", GradeStatus.PASSED);


        when(studentExamService.getAll()).thenReturn(List.of(studentExam));
        when(studentExamMapper.toDto(studentExam)).thenReturn(dto);

        mockMvc.perform(get("/api/studentexams"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(id.toString()))
            .andExpect(jsonPath("$[0].studentId").value(studentId.toString()))
            .andExpect(jsonPath("$[0].sessionGrade").value("9"))
            .andExpect(jsonPath("$[0].resitGrade").value("10"));
    }

    @Test
    void shouldReturnStudentExamById() throws Exception {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        StudentExam studentExam = new StudentExam(id, studentId, examId, "9", "10", GradeStatus.PASSED);
        StudentExamDto dto = new StudentExamDto(id, studentId, examId, "9", "10", GradeStatus.PASSED);

        when(studentExamService.getById(id)).thenReturn(Optional.of(studentExam));
        when(studentExamMapper.toDto(studentExam)).thenReturn(dto);

        mockMvc.perform(get("/api/studentexams/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.studentId").value(studentId.toString()));
    }

    @Test
    void shouldReturn404WhenStudentExamDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        when(studentExamService.getById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/studentexams/" + id))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateStudentExam() throws Exception {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        StudentExam studentExam = new StudentExam(id, studentId, examId, "9", "10", GradeStatus.PASSED);
        StudentExamDto dto = new StudentExamDto(id, studentId, examId, "9", "10", GradeStatus.PASSED);

        when(studentExamMapper.toEntity(any())).thenReturn(studentExam);
        when(studentExamService.create(any())).thenReturn(studentExam);
        when(studentExamMapper.toDto(studentExam)).thenReturn(dto);

        mockMvc.perform(post("/api/studentexams")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                          "studentId": "%s",
                          "examId": "%s",
                          "sessionGrade": "9",
                          "resitGrade": "10",
                          "gradeStatus": "PASSED"
                        }
                        """, studentId, examId)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.studentId").value(studentId.toString()))
            .andExpect(jsonPath("$.sessionGrade").value("9"));
    }

    @Test
    void shouldUpdateStudentExam() throws Exception {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        StudentExam studentExam = new StudentExam(id, studentId, examId, "10", "10", GradeStatus.PASSED);
        StudentExamDto dto = new StudentExamDto(id, studentId, examId, "10", "10", GradeStatus.PASSED);

        when(studentExamMapper.toEntity(any())).thenReturn(studentExam);
        when(studentExamService.update(eq(id), any())).thenReturn(Optional.of(studentExam));
        when(studentExamMapper.toDto(studentExam)).thenReturn(dto);

        mockMvc.perform(put("/api/studentexams/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                          "studentId": "%s",
                          "examId": "%s",
                          "sessionGrade": "10",
                          "resitGrade": "10",
                          "gradeStatus": "PASSED"
                        }
                        """, studentId, examId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionGrade").value("10"));
    }

    @Test
    void shouldReturn404WhenUpdatingUnknownStudentExam() throws Exception {
        UUID id = UUID.randomUUID();

        when(studentExamMapper.toEntity(any())).thenReturn(new StudentExam());
        when(studentExamService.update(eq(id), any())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/studentexams/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "sessionGrade": "10"
                        }
                        """))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteStudentExam() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/studentexams/" + id))
            .andExpect(status().isOk());

        verify(studentExamService).delete(id);
    }
}

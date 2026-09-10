package cloudflight.integra.backend.examregistration;

import cloudflight.integra.backend.examregistration.model.ExamRegistration;
import cloudflight.integra.backend.examregistration.model.ExamRegistrationDto;
import cloudflight.integra.backend.examregistration.model.ExamSlot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExamRegistrationController.class)
class ExamRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExamRegistrationService service;

    @MockitoBean
    private ExamRegistrationMapper mapper;

    @Test
    void shouldReturnRegistrationsByStudent() throws Exception {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        ExamRegistration registration = new ExamRegistration(id, studentId, examId, ExamSlot.PRIMARY);
        ExamRegistrationDto dto = new ExamRegistrationDto(id, studentId, examId, ExamSlot.PRIMARY);

        when(service.getByStudent(studentId)).thenReturn(List.of(registration));
        when(mapper.toDto(registration)).thenReturn(dto);

        mockMvc.perform(get("/api/exam-registrations/student/" + studentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(id.toString()))
            .andExpect(jsonPath("$[0].examId").value(examId.toString()))
            .andExpect(jsonPath("$[0].chosenSlot").value("PRIMARY"));
    }

    @Test
    void shouldCreateRegistration() throws Exception {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID examId = UUID.randomUUID();

        ExamRegistration registration = new ExamRegistration(id, studentId, examId, ExamSlot.PRIMARY);
        ExamRegistrationDto dto = new ExamRegistrationDto(id, studentId, examId, ExamSlot.PRIMARY);

        when(mapper.toEntity(any())).thenReturn(registration);
        when(service.register(any())).thenReturn(registration);
        when(mapper.toDto(registration)).thenReturn(dto);

        String body = """
            {
              "studentId": "%s",
              "examId": "%s",
              "chosenSlot": "PRIMARY"
            }
            """.formatted(studentId, examId);

        mockMvc.perform(post("/api/exam-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.chosenSlot").value("PRIMARY"));
    }
}

package cloudflight.integra.backend.advisorrequest;

import cloudflight.integra.backend.advisorrequest.model.AdvisorRequest;
import cloudflight.integra.backend.advisorrequest.model.AdvisorRequestDto;
import cloudflight.integra.backend.advisorrequest.model.AdvisorRequestStatus;
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

@WebMvcTest(AdvisorRequestController.class)
class AdvisorRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdvisorRequestService service;

    @MockitoBean
    private AdvisorRequestMapper mapper;

    @Test
    void shouldReturnRequestsByStudent() throws Exception {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        AdvisorRequest request =
            new AdvisorRequest(id, studentId, teacherId, "msg", AdvisorRequestStatus.PENDING);
        AdvisorRequestDto dto =
            new AdvisorRequestDto(id, studentId, teacherId, "msg", AdvisorRequestStatus.PENDING);

        when(service.getByStudent(studentId)).thenReturn(List.of(request));
        when(mapper.toDto(request)).thenReturn(dto);

        mockMvc.perform(get("/api/advisor-requests/student/" + studentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].teacherId").value(teacherId.toString()))
            .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void shouldReturnRequestsByTeacher() throws Exception {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        AdvisorRequest request =
            new AdvisorRequest(id, studentId, teacherId, "msg", AdvisorRequestStatus.PENDING);
        AdvisorRequestDto dto =
            new AdvisorRequestDto(id, studentId, teacherId, "msg", AdvisorRequestStatus.PENDING);

        when(service.getByTeacher(teacherId)).thenReturn(List.of(request));
        when(mapper.toDto(request)).thenReturn(dto);

        mockMvc.perform(get("/api/advisor-requests/teacher/" + teacherId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].studentId").value(studentId.toString()));
    }

    @Test
    void shouldCreateRequest() throws Exception {
        UUID id = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        AdvisorRequest request =
            new AdvisorRequest(id, studentId, teacherId, "msg", AdvisorRequestStatus.PENDING);
        AdvisorRequestDto dto =
            new AdvisorRequestDto(id, studentId, teacherId, "msg", AdvisorRequestStatus.PENDING);

        when(mapper.toEntity(any())).thenReturn(request);
        when(service.apply(any())).thenReturn(request);
        when(mapper.toDto(request)).thenReturn(dto);

        String body = """
            {
              "studentId": "%s",
              "teacherId": "%s",
              "message": "msg"
            }
            """.formatted(studentId, teacherId);

        mockMvc.perform(post("/api/advisor-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldApproveRequest() throws Exception {
        UUID id = UUID.randomUUID();

        AdvisorRequest request =
            new AdvisorRequest(id, UUID.randomUUID(), UUID.randomUUID(), "msg", AdvisorRequestStatus.APPROVED);
        AdvisorRequestDto dto =
            new AdvisorRequestDto(id, request.getStudentId(), request.getTeacherId(), "msg", AdvisorRequestStatus.APPROVED);

        when(service.approve(id)).thenReturn(request);
        when(mapper.toDto(request)).thenReturn(dto);

        mockMvc.perform(put("/api/advisor-requests/" + id + "/approve"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldRejectRequest() throws Exception {
        UUID id = UUID.randomUUID();

        AdvisorRequest request =
            new AdvisorRequest(id, UUID.randomUUID(), UUID.randomUUID(), "msg", AdvisorRequestStatus.REJECTED);
        AdvisorRequestDto dto =
            new AdvisorRequestDto(id, request.getStudentId(), request.getTeacherId(), "msg", AdvisorRequestStatus.REJECTED);

        when(service.reject(id)).thenReturn(request);
        when(mapper.toDto(request)).thenReturn(dto);

        mockMvc.perform(put("/api/advisor-requests/" + id + "/reject"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("REJECTED"));
    }
}

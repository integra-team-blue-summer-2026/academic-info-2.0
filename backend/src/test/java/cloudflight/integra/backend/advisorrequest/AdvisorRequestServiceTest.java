package cloudflight.integra.backend.advisorrequest;

import cloudflight.integra.backend.advisorrequest.model.AdvisorRequest;
import cloudflight.integra.backend.advisorrequest.model.AdvisorRequestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdvisorRequestServiceTest {

    @Autowired
    private AdvisorRequestService service;

    private AdvisorRequest request(UUID studentId, UUID teacherId) {
        AdvisorRequest r = new AdvisorRequest();
        r.setStudentId(studentId);
        r.setTeacherId(teacherId);
        r.setMessage("As vrea sa ma indrumati la licenta");
        return r;
    }

    @Test
    void shouldCreateValidRequestAsPending() {
        AdvisorRequest saved = service.apply(request(UUID.randomUUID(), UUID.randomUUID()));
        assertEquals(AdvisorRequestStatus.PENDING, saved.getStatus());
    }

    @Test
    void shouldRejectSecondRequestToSameTeacher() {
        UUID studentId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();

        service.apply(request(studentId, teacherId));
        assertThrows(ResponseStatusException.class,
            () -> service.apply(request(studentId, teacherId)));
    }

    @Test
    void shouldRejectMoreThanThreePendingRequests() {
        UUID studentId = UUID.randomUUID();

        service.apply(request(studentId, UUID.randomUUID()));
        service.apply(request(studentId, UUID.randomUUID()));
        service.apply(request(studentId, UUID.randomUUID()));

        assertThrows(ResponseStatusException.class,
            () -> service.apply(request(studentId, UUID.randomUUID())));
    }

    @Test
    void shouldRejectNewRequestWhenStudentAlreadyHasAdvisor() {
        UUID studentId = UUID.randomUUID();

        AdvisorRequest first = service.apply(request(studentId, UUID.randomUUID()));
        service.approve(first.getId());

        assertThrows(ResponseStatusException.class,
            () -> service.apply(request(studentId, UUID.randomUUID())));
    }

    @Test
    void shouldCancelOtherPendingRequestsWhenOneIsApproved() {
        UUID studentId = UUID.randomUUID();

        AdvisorRequest r1 = service.apply(request(studentId, UUID.randomUUID()));
        AdvisorRequest r2 = service.apply(request(studentId, UUID.randomUUID()));
        AdvisorRequest r3 = service.apply(request(studentId, UUID.randomUUID()));

        service.approve(r1.getId());

        var all = service.getByStudent(studentId);
        assertEquals(AdvisorRequestStatus.APPROVED,
            all.stream().filter(r -> r.getId().equals(r1.getId())).findFirst().orElseThrow().getStatus());
        assertEquals(AdvisorRequestStatus.CANCELLED,
            all.stream().filter(r -> r.getId().equals(r2.getId())).findFirst().orElseThrow().getStatus());
        assertEquals(AdvisorRequestStatus.CANCELLED,
            all.stream().filter(r -> r.getId().equals(r3.getId())).findFirst().orElseThrow().getStatus());
    }

    @Test
    void shouldRejectRequest() {
        AdvisorRequest r = service.apply(request(UUID.randomUUID(), UUID.randomUUID()));
        AdvisorRequest rejected = service.reject(r.getId());
        assertEquals(AdvisorRequestStatus.REJECTED, rejected.getStatus());
    }

    @Test
    void shouldThrowWhenApprovingUnknownRequest() {
        assertThrows(ResponseStatusException.class,
            () -> service.approve(UUID.randomUUID()));
    }
}

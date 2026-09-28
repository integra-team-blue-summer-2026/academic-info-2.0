package cloudflight.integra.backend.room;

import cloudflight.integra.backend.room.model.RoomAvailability;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, UUID> {
    List<RoomAvailability> findByRoomId(UUID roomId);
    @Transactional
    void deleteByRoomId(UUID roomId);
}

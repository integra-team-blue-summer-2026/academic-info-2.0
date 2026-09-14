package cloudflight.integra.backend.room.model;

import java.util.UUID;

public class Room {

    private UUID id;
    private String roomName;
    private String zone;

    public Room() {
    }

    public Room(UUID id, String roomName, String zone) {
        this.id = id;
        this.roomName = roomName;
        this.zone = zone;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}

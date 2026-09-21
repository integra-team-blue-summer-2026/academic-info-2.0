package cloudflight.integra.backend.room;

import cloudflight.integra.backend.room.model.RoomAvailabilityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/room-availabilities")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomAvailabilityController {

    private final RoomAvailabilityService service;
    private final RoomMapper mapper;

    public RoomAvailabilityController(
        RoomAvailabilityService service,
        RoomMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get all room availabilities",
        operationId = "getAllRoomAvailabilities"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "All room availabilities",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(
                    schema = @Schema(implementation = RoomAvailabilityDto.class)
                )
            )
        )
    })
    public List<RoomAvailabilityDto> getAll() {
        return service.getAll()
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping(
        value = "/room/{roomId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Get room availabilities by room id",
        operationId = "getRoomAvailabilitiesByRoomId"
    )
    public List<RoomAvailabilityDto> getByRoomId(
        @PathVariable UUID roomId
    ) {
        return service.getByRoomId(roomId)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create room availability",
        operationId = "createRoomAvailability"
    )
    public RoomAvailabilityDto create(
        @RequestBody RoomAvailabilityDto dto
    ) {
        return mapper.toDto(
            service.create(mapper.toEntity(dto))
        );
    }

    @PutMapping(
        value = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Update room availability",
        operationId = "updateRoomAvailability"
    )
    public RoomAvailabilityDto update(
        @PathVariable UUID id,
        @RequestBody RoomAvailabilityDto dto
    ) {
        return service.update(id, mapper.toEntity(dto))
            .map(mapper::toDto)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Delete room availability",
        operationId = "deleteRoomAvailability"
    )
    public void delete(@PathVariable UUID id) {
        if (!service.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

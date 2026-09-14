package cloudflight.integra.backend.room;

import cloudflight.integra.backend.room.model.RoomDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomController {

    private final RoomService service;
    private final RoomMapper mapper;

    public RoomController(RoomService service, RoomMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get all rooms",
        operationId = "getAllRooms"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "All rooms",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(
                    schema = @Schema(implementation = RoomDto.class)
                )
            )
        )
    })
    public List<RoomDto> getAll() {
        return service.getAll()
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping(
        value = "/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Get room by id",
        operationId = "getRoomById"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Room found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Room not found"
        )
    })
    public RoomDto getById(@PathVariable UUID id) {
        return service.getById(id)
            .map(mapper::toDto)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
    }

    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Create room",
        operationId = "createRoom"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Room created",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomDto.class)
            )
        )
    })
    public ResponseEntity<RoomDto> create(
        @RequestBody RoomDto dto
    ) {
        RoomDto created = mapper.toDto(
            service.create(mapper.toEntity(dto))
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(created);
    }

    @PutMapping(
        value = "/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
        summary = "Update room",
        operationId = "updateRoom"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Room updated",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RoomDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Room not found"
        )
    })
    public RoomDto update(
        @PathVariable UUID id,
        @RequestBody RoomDto dto
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
        summary = "Delete room",
        operationId = "deleteRoom"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Room deleted"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Room not found"
        )
    })
    public void delete(@PathVariable UUID id) {
        if (!service.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

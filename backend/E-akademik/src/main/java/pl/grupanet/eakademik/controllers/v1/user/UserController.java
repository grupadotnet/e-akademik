package pl.grupanet.eakademik.controllers.v1.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Tag(name = "User", description = "Resident and staff profile retrieval and updates")
class UserController {

    private final UserProfileRepository userProfileRepository;

    @Operation(
            summary = "Update a resident profile (STAFF and ADMIN only)",
            description = "Updates an existing dormitory resident profile (name, surname, dormitory, and room number). " +
                    "Requires an active session with the STAFF or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resident profile successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResidentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data, non-existent dormitory, or resident not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Access denied (STAFF or ADMIN role required)", content = @Content)
    })
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @PutMapping("/resident/{id}")
    ResponseEntity<ResidentResponse> updateResident(
            @Parameter(description = "Resident UUID", required = true) @PathVariable UUID id,
            @RequestBody UpdateResidentRequest request
    ) {
        ResidentId residentId = new ResidentId(id);
        Name name = new Name(request.name());
        Name surname = new Name(request.surname());
        Name dormitoryName = new Name(request.dormitoryName());
        UniversityNames universityName = request.universityName();
        DormitoryId dormitoryId = new DormitoryId(universityName, dormitoryName);
        RoomNumber roomNumber = new RoomNumber(request.roomNumber());

        ResidentDto residentDto = new ResidentDto(residentId, name, surname, dormitoryId, roomNumber);
        ResidentDto updatedDto = userProfileRepository.updateResident(residentDto);

        return ResponseEntity.ok(dtoToResponse(updatedDto));
    }

    @Operation(
            summary = "Update a staff profile (ADMIN only)",
            description = "Updates an existing administration staff profile (name, surname, and dormitory). " +
                    "Requires an active session with the ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff profile successfully updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StaffResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data, non-existent dormitory, or staff member not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Access denied (ADMIN role required)", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/staff/{id}")
    ResponseEntity<StaffResponse> updateStaff(
            @Parameter(description = "Staff member UUID", required = true) @PathVariable UUID id,
            @RequestBody UpdateStaffRequest request
    ) {
        StaffId staffId = new StaffId(id);
        Name name = new Name(request.name());
        Name surname = new Name(request.surname());
        Name dormitoryName = new Name(request.dormitoryName());
        UniversityNames universityName = request.universityName();
        DormitoryId dormitoryId = new DormitoryId(universityName, dormitoryName);

        StaffDto staffDto = new StaffDto(staffId, name, surname, dormitoryId);
        StaffDto updatedDto = userProfileRepository.updateStaff(staffDto);

        return ResponseEntity.ok(dtoToResponse(updatedDto));
    }

    @Operation(
            summary = "Get a resident profile by ID",
            description = "Returns the profile details of a dormitory resident by their UUID. Requires authentication."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resident profile found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResidentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Resident not found or invalid UUID",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Authentication required", content = @Content)
    })
    @GetMapping("/resident/{id}")
    ResponseEntity<ResidentResponse> getResident(
            @Parameter(description = "Resident UUID", required = true) @PathVariable UUID id
    ) {
        ResidentId residentId = new ResidentId(id);
        ResidentDto residentDto = userProfileRepository.findByResidentId(residentId);

        return ResponseEntity.ok(dtoToResponse(residentDto));
    }

    @Operation(
            summary = "Get a staff profile by ID",
            description = "Returns the profile details of an administration staff member by their UUID. Requires authentication."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff profile found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StaffResponse.class))),
            @ApiResponse(responseCode = "400", description = "Staff member not found or invalid UUID",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Authentication required", content = @Content)
    })
    @GetMapping("/staff/{id}")
    ResponseEntity<StaffResponse> getStaff(
            @Parameter(description = "Staff member UUID", required = true) @PathVariable UUID id
    ) {
        StaffId staffId = new StaffId(id);
        StaffDto staffDto = userProfileRepository.findByStaffId(staffId);

        return ResponseEntity.ok(dtoToResponse(staffDto));
    }

    @Operation(
            summary = "Get multiple resident profiles by IDs",
            description = "Returns a map of resident UUIDs to resident profiles for the requested IDs. Missing IDs are omitted. Requires authentication."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resident profiles retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Authentication required", content = @Content)
    })
    @GetMapping("/residents")
    ResponseEntity<Map<UUID, ResidentResponse>> getResidents(
            @Parameter(description = "List of resident UUIDs", required = true) @RequestParam List<UUID> ids
    ) {
        List<ResidentId> residentIds = new ArrayList<>();
        for (UUID id : ids) {
            residentIds.add(new ResidentId(id));
        }

        Map<ResidentId, ResidentDto> map = userProfileRepository.findByResidentIds(residentIds);

        Map<UUID, ResidentResponse> residents = new HashMap<>();
        map.forEach((residentId, residentDto) -> residents.put(residentId.getIdAsUUID(), dtoToResponse(residentDto)));

        return ResponseEntity.ok(residents);
    }

    @Operation(
            summary = "Get multiple staff profiles by IDs",
            description = "Returns a map of staff UUIDs to staff profiles for the requested IDs. Missing IDs are omitted. Requires authentication."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Staff profiles retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Authentication required", content = @Content)
    })
    @GetMapping("/staff")
    ResponseEntity<Map<UUID, StaffResponse>> getStaffs(
            @Parameter(description = "List of staff member UUIDs", required = true) @RequestParam List<UUID> ids
    ) {
        List<StaffId> staffIds = new ArrayList<>();
        for (UUID id : ids) {
            staffIds.add(new StaffId(id));
        }

        Map<StaffId, StaffDto> map = userProfileRepository.findByStaffIds(staffIds);

        Map<UUID, StaffResponse> staff = new HashMap<>();
        map.forEach((staffId, staffDto) -> staff.put(staffId.getIdAsUUID(), dtoToResponse(staffDto)));

        return ResponseEntity.ok(staff);
    }

    private ResidentResponse dtoToResponse(ResidentDto residentDto) {
        return new ResidentResponse(
                residentDto.id().getIdAsUUID(),
                residentDto.name().toString(),
                residentDto.surname().toString(),
                residentDto.dormitoryId().getUniversityName(),
                residentDto.dormitoryId().getDormitoryName().toString(),
                residentDto.roomNumber().toString()
        );
    }

    private StaffResponse dtoToResponse(StaffDto staffDto) {
        return new StaffResponse(
                staffDto.id().getIdAsUUID(),
                staffDto.name().toString(),
                staffDto.surname().toString(),
                staffDto.dormitoryId().getUniversityName(),
                staffDto.dormitoryId().getDormitoryName().toString()
        );
    }
}

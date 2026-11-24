package pl.grupanet.eakademik.controllers.v1.user;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.util.UUID;

@Schema(description = "Dormitory resident profile response")
record ResidentResponse(
        @Schema(description = "Resident unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Resident first name", example = "Jan")
        String name,

        @Schema(description = "Resident surname", example = "Kowalski")
        String surname,

        @Schema(description = "University identifier", example = "POLITECHNIKA_KRAKOWSKA")
        UniversityNames universityName,

        @Schema(description = "Dormitory name", example = "Ds-3 Bartek")
        String dormitoryName,

        @Schema(description = "Resident room number", example = "204B")
        String roomNumber
) {}

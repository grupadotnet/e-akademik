package pl.grupanet.eakademik.controllers.v1.user;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

@Schema(description = "Request payload for updating a dormitory resident profile")
record UpdateResidentRequest(
        @Schema(description = "University identifier", example = "POLITECHNIKA_KRAKOWSKA", requiredMode = Schema.RequiredMode.REQUIRED)
        UniversityNames universityName,

        @Schema(description = "Name or symbol of the dormitory", example = "Ds-3 Bartek", requiredMode = Schema.RequiredMode.REQUIRED)
        String dormitoryName,

        @Schema(description = "Resident room number", example = "204B", requiredMode = Schema.RequiredMode.REQUIRED)
        String roomNumber,

        @Schema(description = "Resident first name", example = "Jan", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Resident surname", example = "Kowalski", requiredMode = Schema.RequiredMode.REQUIRED)
        String surname
) {}

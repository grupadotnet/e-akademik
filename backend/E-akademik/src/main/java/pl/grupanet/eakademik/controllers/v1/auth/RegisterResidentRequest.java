package pl.grupanet.eakademik.controllers.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for registering a new dormitory resident")
record RegisterResidentRequest(
        @Schema(description = "Name of the university", example = "Politechnika Krakowska", requiredMode = Schema.RequiredMode.REQUIRED)
        String universityName,

        @Schema(description = "Name or symbol of the dormitory", example = "DS1", requiredMode = Schema.RequiredMode.REQUIRED)
        String dormitoryName,

        @Schema(description = "Resident room number", example = "101A", requiredMode = Schema.RequiredMode.REQUIRED)
        String roomNumber,

        @Schema(description = "Resident full name", example = "Jan Kowalski", requiredMode = Schema.RequiredMode.REQUIRED)
        String fullName,

        @Schema(description = "Resident email address", example = "jan.kowalski@student.pk.edu.pl", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "Account password", example = "SecretPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}
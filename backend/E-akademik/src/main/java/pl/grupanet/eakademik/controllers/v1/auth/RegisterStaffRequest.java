package pl.grupanet.eakademik.controllers.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

@Schema(description = "Request payload for registering a new dormitory staff member")
record RegisterStaffRequest(
        @Schema(description = "Name of the university", example = "POLITECHNIKA_KRAKOWSKA", requiredMode = Schema.RequiredMode.REQUIRED)
        UniversityNames universityName,

        @Schema(description = "Name or symbol of the dormitory", example = "DS1", requiredMode = Schema.RequiredMode.REQUIRED)
        String dormitoryName,

        @Schema(description = "Staff name", example = "Jan", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "Staff surname", example = "Kowalski", requiredMode = Schema.RequiredMode.REQUIRED)
        String surname,

        @Schema(description = "Staff member email address", example = "jan.kowalski@staff.pk.edu.pl", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "Account password", example = "SecretPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) implements RegistrationRequest {}
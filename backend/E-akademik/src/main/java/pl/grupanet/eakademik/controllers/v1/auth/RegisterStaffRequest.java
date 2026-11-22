package pl.grupanet.eakademik.controllers.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for registering a new dormitory staff member")
record RegisterStaffRequest(
        @Schema(description = "Name of the university", example = "Politechnika Krakowska", requiredMode = Schema.RequiredMode.REQUIRED)
        String universityName,

        @Schema(description = "Name or symbol of the dormitory", example = "DS1", requiredMode = Schema.RequiredMode.REQUIRED)
        String dormitoryName,

        @Schema(description = "Staff member full name", example = "Anna Nowak", requiredMode = Schema.RequiredMode.REQUIRED)
        String fullName,

        @Schema(description = "Staff member email address", example = "anna.nowak@staff.pk.edu.pl", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "Account password", example = "SecretPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}
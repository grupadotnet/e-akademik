package pl.grupanet.eakademik.controllers.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for user authentication")
record LoginRequest(
        @Schema(description = "User email address", example = "john.doe@student.edu.pl", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "User password", example = "SecretPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
package pl.grupanet.eakademik.controllers.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returning JWT token for user")
record LoginResponse(
        @Schema(description = "JWT token")
        String token) {
}

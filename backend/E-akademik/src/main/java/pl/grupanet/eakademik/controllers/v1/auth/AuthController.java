package pl.grupanet.eakademik.controllers.v1.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.commons.Email;
import org.commons.Password;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.grupanet.eakademik.authentication.AuthService;
import pl.grupanet.eakademik.authentication.CookieService;
import pl.grupanet.eakademik.controllers.v1.ApiV1Constants;

@RestController
@RequestMapping(ApiV1Constants.API_URL + "auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, and JWT cookie session management")
class AuthController {
    private final AuthService authService;
    private final CookieService cookieService;

    @Operation(
            summary = "Register a resident (STAFF only)",
            description = "Creates a new dormitory resident account using an email address and password. " +
                    "Requires an active session of a user with the STAFF role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resident account successfully created", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid email or password format",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "409", description = "User with the given email already exists",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/register/resident")
    ResponseEntity<Void> registerResident(@RequestBody RegisterResidentRequest registerResidentRequest) {
        Email email = new Email(registerResidentRequest.email());
        Password password = new Password(registerResidentRequest.password());

        authService.registerResident(email, password);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Register a staff member (ADMIN only)",
            description = "Creates a new administration staff account. " +
                    "Requires an active session of a user with the ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Staff account successfully created", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid email or password format",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "403", description = "Access denied (ADMIN role required)", content = @Content),
            @ApiResponse(responseCode = "409", description = "User with the given email already exists",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/register/staff")
    ResponseEntity<Void> registerStaff(@RequestBody RegisterStaffRequest registerStaffRequest) {
        Email email = new Email(registerStaffRequest.email());
        Password password = new Password(registerStaffRequest.password());

        authService.registerStaff(email, password);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates the user and stores the JWT token in an HttpOnly `jwt_token` cookie valid for 24 hours."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully authenticated — JWT token returned in the Set-Cookie header",
                    headers = @Header(name = HttpHeaders.SET_COOKIE, description = "HttpOnly `jwt_token` cookie containing the JWT"),
                    content = @Content
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(mediaType = "text/plain", schema = @Schema(example = "Invalid credentials")))
    })
    @PostMapping("/login")
    ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        Email email = new Email(loginRequest.email());
        Password password = new Password(loginRequest.password());

        String token = authService.loginUser(email, password);
        httpServletResponse.addCookie(cookieService.createJwtCookie(token, 60 * 60 * 24));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Log out user",
            description = "Clears the `jwt_token` cookie by setting `Max-Age=0`."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully logged out",
                    headers = @Header(name = HttpHeaders.SET_COOKIE, description = "Expired `jwt_token` cookie"),
                    content = @Content
            )
    })
    @PostMapping("/logout")
    ResponseEntity<Void> logout(HttpServletResponse httpServletResponse) {
        httpServletResponse.addCookie(cookieService.createJwtCookie(null, 0));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

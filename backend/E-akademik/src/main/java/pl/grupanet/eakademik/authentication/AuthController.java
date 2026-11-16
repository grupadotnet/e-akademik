package pl.grupanet.eakademik.authentication;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.AuthenticationException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
class AuthController {
    private final AuthService authService;

    @PostMapping("/register/resident")
    ResponseEntity<Void> registerResident(@RequestBody RegisterResidentRequest registerResidentRequest) {
        authService.registerResident(registerResidentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register/staff")
    ResponseEntity<Void> registerStaff(@RequestBody RegisterStaffRequest registerStaffRequest) {
        authService.registerStaff(registerStaffRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        String token = authService.loginUser(loginRequest);
        httpServletResponse.addCookie(authService.createJwtCookie(token, 60*60*24));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(HttpServletResponse httpServletResponse) {
        httpServletResponse.addCookie(authService.createJwtCookie(null, 0));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    ResponseEntity<String> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    ResponseEntity<String> handleConflict(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<String> handleUnauthorized(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}

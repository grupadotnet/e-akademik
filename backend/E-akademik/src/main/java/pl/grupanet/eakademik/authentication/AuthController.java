package pl.grupanet.eakademik.authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        httpServletResponse.addCookie(createJwtCookie(token, 60*60*24));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout(HttpServletResponse httpServletResponse) {
        httpServletResponse.addCookie(createJwtCookie(null, 0));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private Cookie createJwtCookie(String token, int maxAgeSeconds) {
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        return cookie;
    }
}

package pl.grupanet.eakademik.authentication;

import jakarta.servlet.http.Cookie;
import org.commons.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.authentication.jwt.TokenService;
import pl.grupanet.eakademik.authentication.repositories.RegisteredUser;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;

@Service
class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Transactional
    void registerResident(RegisterResidentRequest registerResidentRequest) {
        registerUser(
                registerResidentRequest.email(),
                registerResidentRequest.password(),
                Role.RESIDENT
        );
    }

    @Transactional
    void registerStaff(RegisterStaffRequest registerStaffRequest) {
        registerUser(
                registerStaffRequest.email(),
                registerStaffRequest.password(),
                Role.STAFF
        );
    }

    private void registerUser(
            String email,
            String password,
            Role role
    ) {
        Email validEmail = new Email(email);

        if(password == null || password.isBlank() || password.length() < 8) {
            throw new IllegalArgumentException("Password length should be at least 8 characters");
        }

        if(userRepository.existsByEmail(validEmail)) {
            throw new UserAlreadyExistsException(validEmail);
        }

        RegisteredUser user = new RegisteredUser(
                UserId.generate(),
                validEmail,
                passwordEncoder.encode(password),
                role
        );

        userRepository.save(user);
    }

    String loginUser(LoginRequest request) {
        Email validEmail = new Email(request.email());

        if (request.password() == null || request.password().isBlank()) {
            throw new BadCredentialsException("Invalid credentials");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(validEmail.toString(), request.password())
        );

        RegisteredUser user = userRepository.findByEmail(validEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        return tokenService.generateToken(user.id(), user.email(), user.role());
    }

    Cookie createJwtCookie(String token, int maxAgeSeconds) {
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        return cookie;
    }

}

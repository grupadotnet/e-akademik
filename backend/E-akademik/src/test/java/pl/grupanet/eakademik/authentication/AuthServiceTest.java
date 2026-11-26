package pl.grupanet.eakademik.authentication;

import org.commons.Role;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.commons.users.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.grupanet.eakademik.authentication.jwt.TokenService;
import pl.grupanet.eakademik.authentication.repositories.RegisteredUser;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private TokenService tokenService;
    private AppAuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        tokenService = mock(TokenService.class);

        authService = new AppAuthService(
                userRepository,
                passwordEncoder,
                authenticationManager,
                tokenService
        );
    }

    @Test
    void shouldRegisterResidentWithEncodedPasswordAndResidentRole() {
        Email email = new Email("Jan@Example.com");
        Password password = new Password("secret123");
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");
        when(userRepository.save(any(RegisteredUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResidentId returnedId = authService.registerResident(email, password);

        ArgumentCaptor<RegisteredUser> userCaptor = ArgumentCaptor.forClass(RegisteredUser.class);
        verify(userRepository).save(userCaptor.capture());

        RegisteredUser saved = userCaptor.getValue();
        assertEquals(saved.id().getIdAsUUID(), returnedId.getIdAsUUID());
        assertEquals(new Email("jan@example.com"), saved.email());
        assertEquals("encoded-secret", saved.passwordHash());
        assertEquals(Role.RESIDENT, saved.role());
    }

    @Test
    void shouldRegisterStaffWithEncodedPasswordAndStaffRole() {
        Email email = new Email("staff@example.com");
        Password password = new Password("secret123");
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");
        when(userRepository.save(any(RegisteredUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StaffId returnedId = authService.registerStaff(email, password);

        ArgumentCaptor<RegisteredUser> userCaptor = ArgumentCaptor.forClass(RegisteredUser.class);
        verify(userRepository).save(userCaptor.capture());

        RegisteredUser saved = userCaptor.getValue();
        assertEquals(saved.id().getIdAsUUID(), returnedId.getIdAsUUID());
        assertEquals(new Email("staff@example.com"), saved.email());
        assertEquals("encoded-secret", saved.passwordHash());
        assertEquals(Role.STAFF, saved.role());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        Email email = new Email("jan@example.com");
        Password password = new Password("secret123");
        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerResident(email, password));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldAuthenticateAndReturnJwtTokenOnLogin() {
        Email email = new Email("jan@example.com");
        Password password = new Password("secret123");
        UserId userId = UserId.generate();
        RegisteredUser user = new RegisteredUser(
                userId,
                email,
                "encoded-secret",
                Role.RESIDENT
        );
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenService.generateToken(userId, email, Role.RESIDENT)).thenReturn("jwt-token-value");

        String token = authService.loginUser(new Email("Jan@Example.com"), password);

        assertEquals("jwt-token-value", token);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("jan@example.com", "secret123")
        );
    }

    @Test
    void shouldThrowBadCredentialsWhenAuthenticationFails() {
        Email email = new Email("jan@example.com");
        Password password = new Password("wrongpass1");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.loginUser(email, password));
        verify(tokenService, never()).generateToken(any(), any(), any());
    }

    @Test
    void shouldThrowBadCredentialsWhenUserNotFoundAfterAuthentication() {
        Email email = new Email("missing@example.com");
        Password password = new Password("secret123");
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.loginUser(email, password));
        verify(tokenService, never()).generateToken(any(), any(), any());
    }
}

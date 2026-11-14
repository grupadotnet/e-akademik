package pl.grupanet.eakademik.authentication;

import org.commons.DormitoryId;
import org.commons.Email;
import org.commons.Name;
import org.commons.Role;
import org.commons.RoomNumber;
import org.commons.UserId;
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
import pl.grupanet.eakademik.dormitory.DormitorySpecification;
import pl.grupanet.eakademik.dormitory.repositories.DormitoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private DormitoryRepository dormitoryRepository;
    private TokenService tokenService;
    private AuthService authService;

    private DormitoryId dormitoryId;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        dormitoryRepository = mock(DormitoryRepository.class);
        tokenService = mock(TokenService.class);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                authenticationManager,
                dormitoryRepository,
                tokenService
        );

        dormitoryId = new DormitoryId(new Name("Politechnika Krakowska"), new Name("Ds-3 Bartek"));
    }

    @Test
    void shouldRegisterResidentWithEncodedPasswordAndResidentRole() {
        RegisterResidentRequest request = new RegisterResidentRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "222A",
                "Jan Kowalski",
                "Jan@Example.com",
                "secret123"
        );
        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(userRepository.existsByEmail(new Email("jan@example.com"))).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");

        authService.registerResident(request);

        ArgumentCaptor<RegisteredUser> userCaptor = ArgumentCaptor.forClass(RegisteredUser.class);
        verify(userRepository).save(userCaptor.capture());

        RegisteredUser saved = userCaptor.getValue();
        assertEquals(new Email("jan@example.com"), saved.email());
        assertEquals("encoded-secret", saved.passwordHash());
        assertEquals(new Name("Jan Kowalski"), saved.fullName());
        assertEquals(dormitoryId, saved.dormitoryId());
        assertEquals(new RoomNumber("222A"), saved.roomNumber());
        assertEquals(Role.RESIDENT, saved.role());
    }

    @Test
    void shouldRegisterStaffWithNullRoomNumberAndStaffRole() {
        RegisterStaffRequest request = new RegisterStaffRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "Anna Nowak",
                "staff@example.com",
                "secret123"
        );
        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(userRepository.existsByEmail(new Email("staff@example.com"))).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-secret");

        authService.registerStaff(request);

        ArgumentCaptor<RegisteredUser> userCaptor = ArgumentCaptor.forClass(RegisteredUser.class);
        verify(userRepository).save(userCaptor.capture());

        RegisteredUser saved = userCaptor.getValue();
        assertNull(saved.roomNumber());
        assertEquals(Role.STAFF, saved.role());
    }

    @Test
    void shouldThrowExceptionWhenDormitoryDoesNotExist() {
        RegisterResidentRequest request = new RegisterResidentRequest(
                "Unknown Uni",
                "Unknown Dorm",
                "101A",
                "Jan Kowalski",
                "jan@example.com",
                "secret123"
        );
        when(dormitoryRepository.get(any())).thenThrow(new IllegalArgumentException("Dormitory does not exist"));

        assertThrows(IllegalArgumentException.class, () -> authService.registerResident(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterResidentRequest request = new RegisterResidentRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "101B",
                "Jan Kowalski",
                "jan@example.com",
                "secret123"
        );
        when(dormitoryRepository.get(dormitoryId)).thenReturn(mock(DormitorySpecification.class));
        when(userRepository.existsByEmail(new Email("jan@example.com"))).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerResident(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsTooShort() {
        RegisterResidentRequest request = new RegisterResidentRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "101A",
                "Jan Kowalski",
                "jan@example.com",
                "short"
        );

        assertThrows(IllegalArgumentException.class, () -> authService.registerResident(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldAuthenticateAndReturnJwtTokenOnLogin() {
        Email email = new Email("jan@example.com");
        UserId userId = UserId.generate();
        RegisteredUser user = new RegisteredUser(
                userId,
                email,
                "encoded-secret",
                new Name("Jan Kowalski"),
                dormitoryId,
                new RoomNumber("101"),
                Role.RESIDENT
        );
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(tokenService.generateToken(userId, email, Role.RESIDENT)).thenReturn("jwt-token-value");

        String token = authService.loginUser(new LoginRequest("Jan@Example.com", "secret123"));

        assertEquals("jwt-token-value", token);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("jan@example.com", "secret123")
        );
    }

    @Test
    void shouldThrowBadCredentialsWhenLoginPasswordIsBlank() {
        assertThrows(BadCredentialsException.class, () ->
                authService.loginUser(new LoginRequest("jan@example.com", "   "))
        );
        verify(authenticationManager, never()).authenticate(any());
    }
}

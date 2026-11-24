package pl.grupanet.eakademik.controllers.v1.auth;

import org.commons.DormitoryId;
import org.commons.Email;
import org.commons.Name;
import org.commons.Role;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;
import org.commons.users.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.grupanet.eakademik.authentication.repositories.RegisteredUser;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;
import pl.grupanet.eakademik.controllers.ApiVersions;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.register.RegisterResidentRequest;
import pl.grupanet.eakademik.register.RegisterStaffRequest;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.jpa.hibernate.ddl-auto=create-drop"
)
@Testcontainers
class AuthE2ETest {
    private static final String API_URL = ApiVersions.V1.getPath();

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCompleteFullAuthFlowAgainstRealPostgresAndHttpServer() {
        userRepository.save(new RegisteredUser(
                UserId.generate(),
                new Email("staff.e2e@example.com"),
                passwordEncoder.encode("staffpass123"),
                Role.STAFF
        ));

        ResponseEntity<Void> staffLoginResponse = restTemplate.postForEntity(
                API_URL + "auth/login",
                new LoginRequest("staff.e2e@example.com", "staffpass123"),
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, staffLoginResponse.getStatusCode());
        HttpHeaders staffAuthHeaders = extractAuthHeaders(staffLoginResponse);

        // Register resident as authenticated STAFF
        RegisterResidentRequest registerRequest = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "204B",
                "Jan",
                "Kowalski",
                "e2e@example.com",
                "password123"
        );
        ResponseEntity<Void> registerResponse = restTemplate.exchange(
                API_URL + "auth/register/resident",
                HttpMethod.POST,
                new HttpEntity<>(registerRequest, staffAuthHeaders),
                Void.class
        );
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());

        RegisteredUser registeredAuth = userRepository.findByEmail(new Email("e2e@example.com")).orElseThrow();
        ResidentDto registeredProfile = userProfileRepository.findByResidentId(
                new ResidentId(registeredAuth.id().getIdAsUUID())
        );
        assertEquals(new Name("Jan"), registeredProfile.name());
        assertEquals(new Name("Kowalski"), registeredProfile.surname());
        assertEquals(
                new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Ds-3 Bartek")),
                registeredProfile.dormitoryId()
        );
        assertEquals(new RoomNumber("204B"), registeredProfile.roomNumber());

        // Login as newly created RESIDENT
        ResponseEntity<Void> loginResponse = restTemplate.postForEntity(
                API_URL + "auth/login",
                new LoginRequest("e2e@example.com", "password123"),
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, loginResponse.getStatusCode());
        HttpHeaders residentAuthHeaders = extractAuthHeaders(loginResponse);

        // RESIDENT cannot access ADMIN-only staff registration endpoint -> 403 FORBIDDEN
        RegisterStaffRequest staffRequest = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "Anna",
                "Nowak",
                "e2e.staff@example.com",
                "password123"
        );
        ResponseEntity<String> staffResponse = restTemplate.exchange(
                API_URL + "auth/register/staff",
                HttpMethod.POST,
                new HttpEntity<>(staffRequest, residentAuthHeaders),
                String.class
        );
        assertEquals(HttpStatus.FORBIDDEN, staffResponse.getStatusCode());

        // Logout clears jwt_token cookie
        ResponseEntity<Void> logoutResponse = restTemplate.exchange(
                API_URL + "auth/logout", HttpMethod.POST, new HttpEntity<>(residentAuthHeaders), Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, logoutResponse.getStatusCode());
        List<String> logoutCookies = logoutResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(logoutCookies);
        assertTrue(logoutCookies.stream().anyMatch(c -> c.startsWith("jwt_token=") && c.contains("Max-Age=0")));
    }

    @Test
    void shouldRollbackAuthUserWhenDormitoryDoesNotExist() {
        userRepository.save(new RegisteredUser(
                UserId.generate(),
                new Email("staff.rollback.e2e@example.com"),
                passwordEncoder.encode("staffpass123"),
                Role.STAFF
        ));

        ResponseEntity<Void> staffLoginResponse = restTemplate.postForEntity(
                API_URL + "auth/login",
                new LoginRequest("staff.rollback.e2e@example.com", "staffpass123"),
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, staffLoginResponse.getStatusCode());
        HttpHeaders staffAuthHeaders = extractAuthHeaders(staffLoginResponse);

        RegisterResidentRequest invalidDormitoryRequest = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Nieistniejacy Akademik",
                "204B",
                "Jan",
                "Kowalski",
                "rollback.test@example.com",
                "password123"
        );

        ResponseEntity<String> response = restTemplate.exchange(
                API_URL + "auth/register/resident",
                HttpMethod.POST,
                new HttpEntity<>(invalidDormitoryRequest, staffAuthHeaders),
                String.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(userRepository.existsByEmail(new Email("rollback.test@example.com")));
    }

    private HttpHeaders extractAuthHeaders(ResponseEntity<?> response) {
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        String jwtSetCookie = cookies.stream()
                .filter(c -> c.startsWith("jwt_token="))
                .findFirst()
                .orElseThrow();
        assertTrue(jwtSetCookie.contains("HttpOnly"));

        String cookieHeader = jwtSetCookie.split(";", 2)[0];
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.add(HttpHeaders.COOKIE, cookieHeader);
        return authHeaders;
    }
}

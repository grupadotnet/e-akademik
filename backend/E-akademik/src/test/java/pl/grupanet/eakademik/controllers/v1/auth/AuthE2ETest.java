package pl.grupanet.eakademik.controllers.v1.auth;

import org.commons.Email;
import org.commons.Role;
import org.commons.users.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.grupanet.eakademik.authentication.repositories.RegisteredUser;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;
import pl.grupanet.eakademik.controllers.ApiVersions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCompleteFullAuthFlowAgainstRealPostgresAndHttpServer() {
        // 1. Unauthenticated request is rejected
        ResponseEntity<String> unauthResponse = restTemplate.getForEntity("/api/protected", String.class);
        assertEquals(HttpStatus.FORBIDDEN, unauthResponse.getStatusCode());

        // 2. Seed STAFF user and authenticate to obtain JWT cookie
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

        // 3. Register resident as authenticated STAFF
        RegisterResidentRequest registerRequest = new RegisterResidentRequest(
                "Politechnika Krakowska", "Ds-3 Bartek", "204B",
                "Jan Kowalski", "e2e@example.com", "password123"
        );
        ResponseEntity<Void> registerResponse = restTemplate.exchange(
                API_URL + "auth/register/resident",
                HttpMethod.POST,
                new HttpEntity<>(registerRequest, staffAuthHeaders),
                Void.class
        );
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());

        // 4. Login as newly created RESIDENT
        ResponseEntity<Void> loginResponse = restTemplate.postForEntity(
                API_URL + "auth/login",
                new LoginRequest("e2e@example.com", "password123"),
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, loginResponse.getStatusCode());
        HttpHeaders residentAuthHeaders = extractAuthHeaders(loginResponse);

        // 5. Access protected path as RESIDENT (passes security -> 404 Not Found)
        ResponseEntity<String> protectedResponse = restTemplate.exchange(
                "/api/protected", HttpMethod.GET, new HttpEntity<>(residentAuthHeaders), String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, protectedResponse.getStatusCode());

        // 6. RESIDENT cannot register STAFF -> 403 Forbidden
        RegisterStaffRequest staffRequest = new RegisterStaffRequest(
                "Politechnika Krakowska", "Ds-3 Bartek",
                "Anna Nowak", "e2e.staff@example.com", "password123"
        );
        ResponseEntity<String> staffResponse = restTemplate.exchange(
                API_URL + "auth/register/staff", HttpMethod.POST, new HttpEntity<>(staffRequest, residentAuthHeaders), String.class
        );
        assertEquals(HttpStatus.FORBIDDEN, staffResponse.getStatusCode());

        // 7. Logout clears jwt_token cookie
        ResponseEntity<Void> logoutResponse = restTemplate.exchange(
                API_URL + "auth/logout", HttpMethod.POST, new HttpEntity<>(residentAuthHeaders), Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, logoutResponse.getStatusCode());
        List<String> logoutCookies = logoutResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(logoutCookies);
        assertTrue(logoutCookies.stream().anyMatch(c -> c.startsWith("jwt_token=") && c.contains("Max-Age=0")));
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
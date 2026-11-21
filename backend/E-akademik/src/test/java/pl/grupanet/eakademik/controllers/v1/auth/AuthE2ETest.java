package pl.grupanet.eakademik.controllers.v1.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.grupanet.eakademik.controllers.ApiConstants;
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

    @Test
    void shouldCompleteFullAuthFlowAgainstRealMySqlAndHttpServer() {
        ResponseEntity<String> unauthResponse = restTemplate.getForEntity("/api/protected", String.class);
        assertEquals(HttpStatus.FORBIDDEN, unauthResponse.getStatusCode());

        RegisterResidentRequest registerRequest = new RegisterResidentRequest(
                "Politechnika Krakowska", "Ds-3 Bartek", "204B",
                "Jan Kowalski", "e2e@example.com", "password123"
        );
        ResponseEntity<Void> registerResponse = restTemplate.postForEntity(
                API_URL+"auth/register/resident", registerRequest, Void.class
        );
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());

        ResponseEntity<Void> loginResponse = restTemplate.postForEntity(
                API_URL+"auth/login",
                new LoginRequest("e2e@example.com", "password123"),
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, loginResponse.getStatusCode());

        List<String> cookies = loginResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        String jwtSetCookie = cookies.stream()
                .filter(c -> c.startsWith("jwt_token="))
                .findFirst()
                .orElseThrow();
        assertTrue(jwtSetCookie.contains("HttpOnly"));

        String cookieHeader = jwtSetCookie.split(";", 2)[0];
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.add(HttpHeaders.COOKIE, cookieHeader);

        ResponseEntity<String> protectedResponse = restTemplate.exchange(
                "/api/protected", HttpMethod.GET, new HttpEntity<>(authHeaders), String.class
        );
        assertEquals(HttpStatus.NOT_FOUND, protectedResponse.getStatusCode());

        RegisterStaffRequest staffRequest = new RegisterStaffRequest(
                "Politechnika Krakowska", "Ds-3 Bartek",
                "Anna Nowak", "e2e.staff@example.com", "password123"
        );
        ResponseEntity<String> staffResponse = restTemplate.exchange(
                API_URL+"auth/register/staff", HttpMethod.POST, new HttpEntity<>(staffRequest, authHeaders), String.class
        );
        assertEquals(HttpStatus.FORBIDDEN, staffResponse.getStatusCode());

        ResponseEntity<Void> logoutResponse = restTemplate.exchange(
                API_URL+"auth/logout", HttpMethod.POST, new HttpEntity<>(authHeaders), Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, logoutResponse.getStatusCode());
        List<String> logoutCookies = logoutResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(logoutCookies);
        assertTrue(logoutCookies.stream().anyMatch(c -> c.startsWith("jwt_token=") && c.contains("Max-Age=0")));
    }
}
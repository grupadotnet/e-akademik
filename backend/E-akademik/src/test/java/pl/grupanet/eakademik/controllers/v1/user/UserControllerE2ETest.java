package pl.grupanet.eakademik.controllers.v1.user;

import org.commons.Role;
import org.commons.users.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.grupanet.eakademik.authentication.Email;
import pl.grupanet.eakademik.authentication.repositories.RegisteredUser;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;
import pl.grupanet.eakademik.controllers.ApiVersions;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.register.RegisterResidentRequest;
import pl.grupanet.eakademik.register.RegisterStaffRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.jpa.hibernate.ddl-auto=create-drop"
)
@Testcontainers
class UserControllerE2ETest {
    private static final String AUTH_API_URL = ApiVersions.V1.getPath() + "auth";
    private static final String USER_API_URL = ApiVersions.V1.getPath() + "user";

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
    void shouldCompleteFullUserProfileFlowAgainstRealPostgresAndHttpServer() {
        // 1. Unauthenticated request is rejected
        ResponseEntity<String> unauthenticatedResponse = restTemplate.getForEntity(
                USER_API_URL + "/resident/{id}", String.class, UUID.randomUUID()
        );
        assertEquals(HttpStatus.FORBIDDEN, unauthenticatedResponse.getStatusCode());

        // 2. Seed ADMIN and authenticate via real HTTP login
        userRepository.save(new RegisteredUser(
                UserId.generate(),
                new Email("admin.user.e2e@example.com"),
                passwordEncoder.encode("adminpass123"),
                Role.ADMIN
        ));
        HttpHeaders adminHeaders = loginAndExtractHeaders("admin.user.e2e@example.com", "adminpass123");

        // 3. ADMIN registers a STAFF member through AuthController
        RegisterStaffRequest registerStaffRequest = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "Anna",
                "Nowak",
                "staff.user.e2e@example.com",
                "staffpass123"
        );
        ResponseEntity<Void> registerStaffResponse = restTemplate.exchange(
                AUTH_API_URL + "/register/staff",
                HttpMethod.POST,
                new HttpEntity<>(registerStaffRequest, adminHeaders),
                Void.class
        );
        assertEquals(HttpStatus.CREATED, registerStaffResponse.getStatusCode());

        UUID staffId = userRepository.findByEmail(new Email("staff.user.e2e@example.com"))
                .orElseThrow()
                .id()
                .getIdAsUUID();

        // 4. ADMIN retrieves, updates, and batch-fetches STAFF profile
        ResponseEntity<StaffResponse> getStaffResponse = restTemplate.exchange(
                USER_API_URL + "/staff/{id}",
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                StaffResponse.class,
                staffId
        );
        assertEquals(HttpStatus.OK, getStaffResponse.getStatusCode());
        assertNotNull(getStaffResponse.getBody());
        assertEquals("Anna", getStaffResponse.getBody().name());
        assertEquals("Nowak", getStaffResponse.getBody().surname());

        UpdateStaffRequest updateStaffRequest = new UpdateStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "Anna",
                "Kowalska"
        );
        ResponseEntity<StaffResponse> updateStaffResponse = restTemplate.exchange(
                USER_API_URL + "/staff/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updateStaffRequest, adminHeaders),
                StaffResponse.class,
                staffId
        );
        assertEquals(HttpStatus.OK, updateStaffResponse.getStatusCode());
        assertNotNull(updateStaffResponse.getBody());
        assertEquals("Kowalska", updateStaffResponse.getBody().surname());

        UUID missingStaffId = UUID.randomUUID();
        ResponseEntity<Map<UUID, StaffResponse>> batchStaffResponse = restTemplate.exchange(
                USER_API_URL + "/staff?ids={id1}&ids={id2}",
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                new ParameterizedTypeReference<>() {},
                staffId,
                missingStaffId
        );
        assertEquals(HttpStatus.OK, batchStaffResponse.getStatusCode());
        assertNotNull(batchStaffResponse.getBody());
        assertEquals(1, batchStaffResponse.getBody().size());
        assertEquals("Kowalska", batchStaffResponse.getBody().get(staffId).surname());
        assertFalse(batchStaffResponse.getBody().containsKey(missingStaffId));

        // 5. Log in as STAFF -> forbidden from updating staff, allowed to register & update resident
        HttpHeaders staffHeaders = loginAndExtractHeaders("staff.user.e2e@example.com", "staffpass123");

        ResponseEntity<String> staffUpdatesStaffResponse = restTemplate.exchange(
                USER_API_URL + "/staff/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updateStaffRequest, staffHeaders),
                String.class,
                staffId
        );
        assertEquals(HttpStatus.FORBIDDEN, staffUpdatesStaffResponse.getStatusCode());

        // 6. STAFF registers a RESIDENT
        RegisterResidentRequest registerResidentRequest = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "101A",
                "Jan",
                "Kowalski",
                "resident.user.e2e@example.com",
                "residentpass123"
        );
        ResponseEntity<Void> registerResidentResponse = restTemplate.exchange(
                AUTH_API_URL + "/register/resident",
                HttpMethod.POST,
                new HttpEntity<>(registerResidentRequest, staffHeaders),
                Void.class
        );
        assertEquals(HttpStatus.CREATED, registerResidentResponse.getStatusCode());

        UUID residentId = userRepository.findByEmail(new Email("resident.user.e2e@example.com"))
                .orElseThrow()
                .id()
                .getIdAsUUID();

        // 7. STAFF updates RESIDENT room, fetches single & batch
        UpdateResidentRequest updateResidentRequest = new UpdateResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "Ds-3 Bartek",
                "204B",
                "Jan",
                "Kowalski"
        );
        ResponseEntity<ResidentResponse> updateResidentResponse = restTemplate.exchange(
                USER_API_URL + "/resident/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updateResidentRequest, staffHeaders),
                ResidentResponse.class,
                residentId
        );
        assertEquals(HttpStatus.OK, updateResidentResponse.getStatusCode());
        assertNotNull(updateResidentResponse.getBody());
        assertEquals("204B", updateResidentResponse.getBody().roomNumber());

        ResponseEntity<ResidentResponse> getResidentResponse = restTemplate.exchange(
                USER_API_URL + "/resident/{id}",
                HttpMethod.GET,
                new HttpEntity<>(staffHeaders),
                ResidentResponse.class,
                residentId
        );
        assertEquals(HttpStatus.OK, getResidentResponse.getStatusCode());
        assertNotNull(getResidentResponse.getBody());
        assertEquals("204B", getResidentResponse.getBody().roomNumber());

        UUID missingResidentId = UUID.randomUUID();
        ResponseEntity<Map<UUID, ResidentResponse>> batchResidentResponse = restTemplate.exchange(
                USER_API_URL + "/residents?ids={id1}&ids={id2}",
                HttpMethod.GET,
                new HttpEntity<>(staffHeaders),
                new ParameterizedTypeReference<>() {},
                residentId,
                missingResidentId
        );
        assertEquals(HttpStatus.OK, batchResidentResponse.getStatusCode());
        assertNotNull(batchResidentResponse.getBody());
        assertEquals(1, batchResidentResponse.getBody().size());
        assertEquals("204B", batchResidentResponse.getBody().get(residentId).roomNumber());
        assertFalse(batchResidentResponse.getBody().containsKey(missingResidentId));

        // 8. RESIDENT can read profile, cannot update profile -> 403 FORBIDDEN
        HttpHeaders residentHeaders = loginAndExtractHeaders("resident.user.e2e@example.com", "residentpass123");

        ResponseEntity<ResidentResponse> residentReadOwnResponse = restTemplate.exchange(
                USER_API_URL + "/resident/{id}",
                HttpMethod.GET,
                new HttpEntity<>(residentHeaders),
                ResidentResponse.class,
                residentId
        );
        assertEquals(HttpStatus.OK, residentReadOwnResponse.getStatusCode());

        ResponseEntity<String> residentUpdateForbiddenResponse = restTemplate.exchange(
                USER_API_URL + "/resident/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(updateResidentRequest, residentHeaders),
                String.class,
                residentId
        );
        assertEquals(HttpStatus.FORBIDDEN, residentUpdateForbiddenResponse.getStatusCode());
    }

    private HttpHeaders loginAndExtractHeaders(String email, String password) {
        ResponseEntity<Void> loginResponse = restTemplate.postForEntity(
                AUTH_API_URL + "/login",
                Map.of("email", email, "password", password),
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
        return authHeaders;
    }
}

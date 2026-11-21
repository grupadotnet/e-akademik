package pl.grupanet.eakademik.controllers.v1.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.commons.Email;
import org.commons.Role;
import org.commons.users.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.authentication.repositories.RegisteredUser;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;
import pl.grupanet.eakademik.controllers.ApiVersions;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    private static final String API_URL = ApiVersions.V1.getPath();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterResidentAndReturnCreated() throws Exception {
        RegisterResidentRequest request = new RegisterResidentRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "111B",
                "Jan Kowalski",
                "new.resident@example.com",
                "password123"
        );

        mockMvc.perform(post(API_URL + "auth/register/resident")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertTrue(userRepository.existsByEmail(new Email("new.resident@example.com")));
    }

    @Test
    void shouldForbidStaffRegistrationWithoutAdminRole() throws Exception {
        RegisterStaffRequest request = new RegisterStaffRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "Anna Nowak",
                "unauth.staff@example.com",
                "password123"
        );

        mockMvc.perform(post(API_URL + "auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidStaffRegistrationForResidentRole() throws Exception {
        RegisterResidentRequest residentRequest = new RegisterResidentRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "102A",
                "Jan Kowalski",
                "resident.role@example.com",
                "password123"
        );
        mockMvc.perform(post(API_URL + "auth/register/resident")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(residentRequest)))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post(API_URL + "auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("resident.role@example.com", "password123"))))
                .andExpect(status().isNoContent())
                .andReturn();

        Cookie residentCookie = loginResult.getResponse().getCookie("jwt_token");
        assertNotNull(residentCookie);

        RegisterStaffRequest staffRequest = new RegisterStaffRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "Anna Nowak",
                "resident.tries.staff@example.com",
                "password123"
        );

        mockMvc.perform(post(API_URL + "auth/register/staff")
                        .cookie(residentCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRegisterStaffWhenAdminIsAuthenticated() throws Exception {
        RegisterStaffRequest request = new RegisterStaffRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "Anna Nowak",
                "new.staff@example.com",
                "password123"
        );

        mockMvc.perform(post(API_URL + "auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertTrue(userRepository.existsByEmail(new Email("new.staff@example.com")));
    }

    @Test
    void shouldLoginAndSetHttpOnlyJwtCookie() throws Exception {
        RegisterResidentRequest registerRequest = new RegisterResidentRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "102A",
                "Jan Kowalski",
                "login.user@example.com",
                "password123"
        );

        mockMvc.perform(post(API_URL + "auth/register/resident")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post(API_URL + "auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("login.user@example.com", "password123"))))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("jwt_token"))
                .andExpect(cookie().httpOnly("jwt_token", true))
                .andReturn();

        Cookie jwtCookie = result.getResponse().getCookie("jwt_token");
        assertNotNull(jwtCookie);
        assertEquals(60 * 60 * 24, jwtCookie.getMaxAge());
    }

    @Test
    void shouldAuthenticateAdminViaJwtCookieAndAllowStaffRegistration() throws Exception {
        userRepository.save(new RegisteredUser(
                UserId.generate(),
                new Email("admin@example.com"),
                passwordEncoder.encode("adminpass123"),
                Role.ADMIN
        ));

        MvcResult loginResult = mockMvc.perform(post(API_URL + "auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin@example.com", "adminpass123"))))
                .andExpect(status().isNoContent())
                .andReturn();

        Cookie jwtCookie = loginResult.getResponse().getCookie("jwt_token");
        assertNotNull(jwtCookie);

        RegisterStaffRequest staffRequest = new RegisterStaffRequest(
                "Politechnika Krakowska",
                "Ds-3 Bartek",
                "Staff Member",
                "staff.by.cookie@example.com",
                "password123"
        );

        mockMvc.perform(post(API_URL + "auth/register/staff")
                        .cookie(jwtCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(staffRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldClearJwtCookieOnLogout() throws Exception {
        mockMvc.perform(post(API_URL + "auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().exists("jwt_token"))
                .andExpect(cookie().maxAge("jwt_token", 0))
                .andExpect(cookie().httpOnly("jwt_token", true));
    }
}

package pl.grupanet.eakademik.authentication.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.grupanet.eakademik.authentication.AuthService;
import pl.grupanet.eakademik.authentication.CookieService;
import pl.grupanet.eakademik.controllers.ApiVersions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTest {

    private static final String API_URL = ApiVersions.V1.getPath();

    private static final String VALID_RESIDENT_PAYLOAD = """
            {
                "universityName": "Politechnika Krakowska",
                "dormitoryName": "Ds-3 Bartek",
                "roomNumber": "111B",
                "fullName": "Jan Kowalski",
                "email": "resident@example.com",
                "password": "password123"
            }
            """;

    private static final String VALID_STAFF_PAYLOAD = """
            {
                "universityName": "Politechnika Krakowska",
                "dormitoryName": "Ds-3 Bartek",
                "fullName": "Anna Nowak",
                "email": "staff@example.com",
                "password": "password123"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieService cookieService;

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldAllowResidentRegistrationForStaffRole() throws Exception {
        mockMvc.perform(post(API_URL + "auth/register/resident")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_RESIDENT_PAYLOAD))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "RESIDENT")
    void shouldForbidResidentRegistrationForResidentRole() throws Exception {
        mockMvc.perform(post(API_URL + "auth/register/resident")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_RESIDENT_PAYLOAD))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void shouldForbidResidentRegistrationForAnonymousUser() throws Exception {
        mockMvc.perform(post(API_URL + "auth/register/resident")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_RESIDENT_PAYLOAD))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowStaffRegistrationForAdminRole() throws Exception {
        mockMvc.perform(post(API_URL + "auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_STAFF_PAYLOAD))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldForbidStaffRegistrationForStaffRole() throws Exception {
        mockMvc.perform(post(API_URL + "auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_STAFF_PAYLOAD))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "RESIDENT")
    void shouldForbidStaffRegistrationForResidentRole() throws Exception {
        mockMvc.perform(post(API_URL + "auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_STAFF_PAYLOAD))
                .andExpect(status().isForbidden());
    }
}
package pl.grupanet.eakademik.authentication.security;

import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.grupanet.eakademik.authentication.AuthService;
import pl.grupanet.eakademik.authentication.CookieService;
import pl.grupanet.eakademik.controllers.ApiVersions;
import pl.grupanet.eakademik.user.UserProfileRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationTest {

    private static final String API_URL = ApiVersions.V1.getPath();

    private static final String VALID_RESIDENT_PAYLOAD = """
            {
                "universityName": "POLITECHNIKA_KRAKOWSKA",
                "dormitoryName": "Ds-3 Bartek",
                "roomNumber": "111B",
                "name": "Jan",
                "surname": "Kowalski",
                "email": "resident@example.com",
                "password": "password123"
            }
            """;

    private static final String VALID_STAFF_PAYLOAD = """
            {
                "universityName": "POLITECHNIKA_KRAKOWSKA",
                "dormitoryName": "Ds-3 Bartek",
                "name": "Anna",
                "surname": "Nowak",
                "email": "staff@example.com",
                "password": "password123"
            }
            """;

    private static final String VALID_LOGIN_PAYLOAD = """
            {
                "email": "user@example.com",
                "password": "password123"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private FilterChainProxy filterChainProxy;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieService cookieService;

    @MockitoBean
    private UserProfileRepository userProfileRepository;

    // --- 1. Security beans and filter chain order ---

    @Test
    void shouldRegisterExpectedSecurityBeans() {
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(userDetailsService).isInstanceOf(AppUserDetailsService.class);
        assertThat(authenticationManager).isNotNull();
        assertThat(securityFilterChain).isNotNull();
    }

    @Test
    void shouldRegisterJwtAuthFilterBeforeAuthorizationFilter() {
        List<Filter> filters = filterChainProxy.getFilters("/api/v1/test");

        int jwtFilterIndex = -1;
        int authorizationFilterIndex = -1;

        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i) instanceof JwtAuthFilter) {
                jwtFilterIndex = i;
            }
            if (filters.get(i) instanceof org.springframework.security.web.access.intercept.AuthorizationFilter) {
                authorizationFilterIndex = i;
            }
        }

        assertThat(jwtFilterIndex).isNotNegative();
        assertThat(authorizationFilterIndex).isNotNegative();
        assertThat(jwtFilterIndex).isLessThan(authorizationFilterIndex);
    }

    // --- 2. Public authentication endpoints and CSRF disabled ---

    @Test
    @WithAnonymousUser
    void shouldAllowAnonymousLoginWithoutCsrfToken() throws Exception {
        when(cookieService.createJwtCookie(any(), anyInt()))
                .thenReturn(new Cookie("jwt_token", "dummy"));

        mockMvc.perform(post(API_URL + "auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_LOGIN_PAYLOAD))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithAnonymousUser
    void shouldAllowAnonymousLogoutWithoutCsrfToken() throws Exception {
        when(cookieService.createJwtCookie(isNull(), eq(0)))
                .thenReturn(new Cookie("jwt_token", ""));

        mockMvc.perform(post(API_URL + "auth/logout"))
                .andExpect(status().isNoContent());
    }

    // --- 3. Access rules for /auth/register/resident (STAFF only) ---

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldAllowResidentRegistrationForStaffRole() throws Exception {
        when(authService.registerResident(any(), any())).thenReturn(new ResidentId(UUID.randomUUID()));

        mockMvc.perform(post(API_URL + "auth/register/resident")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_RESIDENT_PAYLOAD))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldForbidResidentRegistrationForAdminRole() throws Exception {
        mockMvc.perform(post(API_URL + "auth/register/resident")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_RESIDENT_PAYLOAD))
                .andExpect(status().isForbidden());
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

    // --- 4. Access rules for /auth/register/staff (ADMIN only) ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowStaffRegistrationForAdminRole() throws Exception {
        when(authService.registerStaff(any(), any())).thenReturn(new StaffId(UUID.randomUUID()));

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

    @Test
    @WithAnonymousUser
    void shouldForbidStaffRegistrationForAnonymousUser() throws Exception {
        mockMvc.perform(post(API_URL + "auth/register/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_STAFF_PAYLOAD))
                .andExpect(status().isForbidden());
    }

    // --- 5. Default protection for unconfigured paths and CORS ---

    @Test
    @WithAnonymousUser
    void shouldForbidAnonymousAccessToProtectedEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/some-protected-endpoint"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void shouldHandleCorsPreflightRequests() throws Exception {
        mockMvc.perform(options(API_URL + "auth/login")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    @WithAnonymousUser
    void shouldAllowSwaggerEndpointsByDefault() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    // --- 6. Swagger disabled via property ---

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @TestPropertySource(properties = "springdoc.swagger-ui.enabled=false")
    class DisabledSwaggerSecurityTest {

        @Autowired
        private MockMvc disabledSwaggerMockMvc;

        @Test
        @WithAnonymousUser
        void shouldForbidSwaggerEndpoints() throws Exception {
            disabledSwaggerMockMvc.perform(get("/v3/api-docs"))
                    .andExpect(status().isForbidden());

            disabledSwaggerMockMvc.perform(get("/swagger-ui/index.html"))
                    .andExpect(status().isForbidden());
        }
    }
}
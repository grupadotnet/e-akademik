package pl.grupanet.eakademik.authentication.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.commons.users.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import pl.grupanet.eakademik.authentication.Email;
import pl.grupanet.eakademik.authentication.jwt.TokenService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthFilterTest {

    private TokenService tokenService;
    private UserDetailsService userDetailsService;
    private FilterChain filterChain;
    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        tokenService = mock(TokenService.class);
        userDetailsService = mock(UserDetailsService.class);
        filterChain = mock(FilterChain.class);
        jwtAuthFilter = new JwtAuthFilter(tokenService, userDetailsService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateFromBearerHeaderAndSetUserIdAsPrincipal() throws ServletException, IOException {
        Email email = new Email("resident@example.com");
        UserId userId = UserId.generate();
        UserDetails userDetails = new User(
                email.toString(),
                "encoded-password",
                List.of(new SimpleGrantedAuthority("ROLE_RESIDENT"))
        );

        when(tokenService.extractEmail("valid-bearer-token")).thenReturn(email);
        when(tokenService.extractUserId("valid-bearer-token")).thenReturn(userId);
        when(userDetailsService.loadUserByUsername(email.toString())).thenReturn(userDetails);
        when(tokenService.isTokenValid("valid-bearer-token", email)).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-bearer-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userId, authentication.getPrincipal());
        assertEquals(userId.toString(), authentication.getName());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_RESIDENT")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldFallbackToJwtCookieWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
        Email email = new Email("cookie@example.com");
        UserId userId = UserId.generate();
        UserDetails userDetails = new User(
                email.toString(),
                "encoded-password",
                List.of(new SimpleGrantedAuthority("ROLE_STAFF"))
        );

        when(tokenService.extractEmail("cookie-token")).thenReturn(email);
        when(tokenService.extractUserId("cookie-token")).thenReturn(userId);
        when(userDetailsService.loadUserByUsername(email.toString())).thenReturn(userDetails);
        when(tokenService.isTokenValid("cookie-token", email)).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt_token", "cookie-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userId, authentication.getPrincipal());
        assertEquals(userId.toString(), authentication.getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldPreferBearerHeaderOverJwtCookieWhenBothArePresent() throws ServletException, IOException {
        Email bearerEmail = new Email("bearer@example.com");
        UserId bearerUserId = UserId.generate();
        UserDetails userDetails = new User(
                bearerEmail.toString(),
                "encoded-password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        when(tokenService.extractEmail("bearer-token")).thenReturn(bearerEmail);
        when(tokenService.extractUserId("bearer-token")).thenReturn(bearerUserId);
        when(userDetailsService.loadUserByUsername(bearerEmail.toString())).thenReturn(userDetails);
        when(tokenService.isTokenValid("bearer-token", bearerEmail)).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bearer-token");
        request.setCookies(new Cookie("jwt_token", "cookie-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(bearerUserId, authentication.getPrincipal());
        verify(tokenService).extractEmail("bearer-token");
        verify(tokenService, never()).extractEmail("cookie-token");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldFallbackToCookieWhenAuthorizationHeaderIsNotBearer() throws ServletException, IOException {
        Email email = new Email("cookie.basic@example.com");
        UserId userId = UserId.generate();
        UserDetails userDetails = new User(
                email.toString(),
                "encoded-password",
                List.of(new SimpleGrantedAuthority("ROLE_RESIDENT"))
        );

        when(tokenService.extractEmail("cookie-token")).thenReturn(email);
        when(tokenService.extractUserId("cookie-token")).thenReturn(userId);
        when(userDetailsService.loadUserByUsername(email.toString())).thenReturn(userDetails);
        when(tokenService.isTokenValid("cookie-token", email)).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz");
        request.setCookies(new Cookie("jwt_token", "cookie-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userId, authentication.getPrincipal());
        verify(tokenService).extractEmail("cookie-token");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenBearerTokenIsInvalid() throws ServletException, IOException {
        when(tokenService.extractEmail("invalid-token")).thenThrow(new JwtException("Invalid token"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenNoHeaderAndNoCookie() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(tokenService, userDetailsService);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotOverrideExistingAuthentication() throws ServletException, IOException {
        Authentication existingAuth = new UsernamePasswordAuthenticationToken(
                UserId.generate(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer bearer-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertSame(existingAuth, SecurityContextHolder.getContext().getAuthentication());
        verify(tokenService, never()).extractEmail(any());
        verifyNoInteractions(userDetailsService);
        verify(filterChain).doFilter(request, response);
    }
}

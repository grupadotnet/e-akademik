package pl.grupanet.eakademik.authentication.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.commons.Email;
import org.commons.users.UserId;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.grupanet.eakademik.authentication.jwt.TokenService;

import java.io.IOException;
import java.util.Objects;

@Slf4j
class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(TokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationToken = extractToken(request);

        if (authorizationToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Email email = tokenService.extractEmail(authorizationToken);
                UserId userId = tokenService.extractUserId(authorizationToken);

                if(email != null && userId != null) {
                    UserDetails user = userDetailsService.loadUserByUsername(email.toString());

                    if(tokenService.isTokenValid(authorizationToken, email)) {
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(userId, null, user.getAuthorities())
                        );
                    }
                }
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
                log.warn("Invalid JWT: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String authorizationToken = null;

        if(authHeader != null &&  authHeader.startsWith("Bearer ")) {
            String normalizedToken = authHeader.substring(7).trim();
            if(!normalizedToken.isBlank()) {
                return normalizedToken;
            }
        }
        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), "jwt_token")) {
                    authorizationToken = cookie.getValue();
                    break;
                }
            }
        }
        return authorizationToken;
    }
}

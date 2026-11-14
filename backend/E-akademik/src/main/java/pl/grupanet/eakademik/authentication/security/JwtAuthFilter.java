package pl.grupanet.eakademik.authentication.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.commons.Email;
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
        Cookie[] cookies = request.getCookies();

        String authorizationToken = null;

        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (Objects.equals(cookie.getName(), "jwt_token")) {
                    authorizationToken = cookie.getValue();
                    break;
                }
            }
        }

        if (authorizationToken != null) {
            try {
                Email email = tokenService.extractEmail(authorizationToken);

                if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails user = userDetailsService.loadUserByUsername(email.toString());

                    if(tokenService.isTokenValid(authorizationToken, email)) {
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                        );
                    }
                }
            } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
                log.warn("Invalid JWT: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}

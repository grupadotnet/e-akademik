package pl.grupanet.eakademik.authentication.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.commons.Email;
import org.commons.Role;
import org.commons.users.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AppJwtServiceTest {

    private static final String TEST_SECRET = "dGhpcy1pcy1hLXRlc3Qtc2VjcmV0LWtleS1mb3ItdW5pdC10ZXN0cy0yNTYtYml0cw==";
    private static final long EXPIRATION_MS = 60_000L;

    private AppJwtService jwtService;
    private UserId userId;
    private Email email;

    @BeforeEach
    void setUp() {
        jwtService = new AppJwtService(TEST_SECRET, EXPIRATION_MS);
        userId = UserId.generate();
        email = new Email("student@example.com");
    }

    @Test
    void shouldGenerateTokenAndExtractEmail() {
        String token = jwtService.generateToken(userId, email, Role.RESIDENT);

        assertNotNull(token);
        assertEquals(email, jwtService.extractEmail(token));
    }

    @Test
    void shouldReturnTrueWhenTokenIsValidForExpectedEmail() {
        String token = jwtService.generateToken(userId, email, Role.RESIDENT);

        assertTrue(jwtService.isTokenValid(token, email));
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotMatchTokenSubject() {
        String token = jwtService.generateToken(userId, email, Role.RESIDENT);

        assertFalse(jwtService.isTokenValid(token, new Email("other@example.com")));
    }

    @Test
    void shouldReturnFalseWhenTokenIsExpired() {
        AppJwtService expiredJwtService = new AppJwtService(TEST_SECRET, -1_000L);
        String expiredToken = expiredJwtService.generateToken(userId, email, Role.RESIDENT);

        assertFalse(jwtService.isTokenValid(expiredToken, email));
    }

    @Test
    void shouldGenerateTokenAndExtractUserId() {
        String token = jwtService.generateToken(userId, email, Role.RESIDENT);

        assertEquals(userId, jwtService.extractUserId(token));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUserIdClaimIsMissing() {
        String tokenWithoutUserId = Jwts.builder()
                .subject(email.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET)))
                .compact();

        assertThrows(IllegalArgumentException.class, () -> jwtService.extractUserId(tokenWithoutUserId));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUserIdClaimIsNotValidUuid() {
        String tokenWithInvalidUserId = Jwts.builder()
                .subject(email.toString())
                .claim("userId", "not-a-uuid")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET)))
                .compact();

        assertThrows(IllegalArgumentException.class, () -> jwtService.extractUserId(tokenWithInvalidUserId));
    }

    @Test
    void shouldReturnFalseWhenTokenIsMalformed() {
        assertFalse(jwtService.isTokenValid("not.a.valid.jwt", email));
    }
}

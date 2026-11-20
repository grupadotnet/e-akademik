package pl.grupanet.eakademik.authentication;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CookieServiceTest {

    private final CookieService cookieService = new CookieService();

    @Test
    void shouldCreateHttpOnlyJwtCookie() {
        Cookie cookie = cookieService.createJwtCookie("jwt-token-value", 3600);

        assertEquals("jwt_token", cookie.getName());
        assertEquals("jwt-token-value", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertEquals(3600, cookie.getMaxAge());
    }

    @Test
    void shouldCreateClearedCookieWhenMaxAgeIsZero() {
        Cookie cookie = cookieService.createJwtCookie(null, 0);

        assertEquals("jwt_token", cookie.getName());
        assertNull(cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertEquals(0, cookie.getMaxAge());
    }
}

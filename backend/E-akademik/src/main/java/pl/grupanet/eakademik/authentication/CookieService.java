package pl.grupanet.eakademik.authentication;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

@Service
class CookieService {

    Cookie createJwtCookie(String token, int maxAgeSeconds) {
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSeconds);
        return cookie;
    }
}

package pl.grupanet.eakademik.authentication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.commons.Email;
import org.commons.Role;
import org.commons.UserId;

import javax.crypto.SecretKey;
import java.util.Date;

class AppJwtService implements TokenService {

    private final SecretKey key;
    private final long expiration;

    AppJwtService(String secret, long expiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }

    @Override
    public String generateToken(UserId userId, Email email, Role role) {
        return Jwts.builder()
                .subject(email.toString())
                .claim("userId", userId.toString())
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    @Override
    public Email extractEmail(String token) {
        return new Email(extractAllClaims(token).getSubject());
    }

    @Override
    public boolean isTokenValid(String token, Email expectedEmail) {
        try {
            Claims claims = extractAllClaims(token);
            Email email = new Email(claims.getSubject());
            return email.equals(expectedEmail) && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

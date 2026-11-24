package pl.grupanet.eakademik.authentication.jwt;

import org.commons.Role;
import org.commons.users.UserId;
import pl.grupanet.eakademik.authentication.Email;

public interface TokenService {
    String generateToken(UserId userId, Email email, Role role);
    Email extractEmail(String token);
    boolean isTokenValid(String token, Email expectedEmail);
}

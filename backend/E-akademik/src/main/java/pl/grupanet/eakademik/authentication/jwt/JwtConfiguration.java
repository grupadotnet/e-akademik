package pl.grupanet.eakademik.authentication.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfiguration {

    private final String secret;
    private final long expiration;

    public JwtConfiguration(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    @Bean
    public TokenService tokenService() {
        return new AppJwtService(secret, expiration);
    }
}

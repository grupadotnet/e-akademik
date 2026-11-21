package pl.grupanet.eakademik.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.grupanet.eakademik.authentication.jwt.TokenService;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;

@Configuration
public class AuthConfiguration {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Autowired
    public AuthConfiguration(UserRepository userRepository, PasswordEncoder passwordEncoder,
                             AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Bean
    public AuthService authService() {
        return new AppAuthService(userRepository, passwordEncoder, authenticationManager, tokenService);
    }
}

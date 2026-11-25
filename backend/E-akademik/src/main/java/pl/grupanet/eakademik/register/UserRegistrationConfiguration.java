package pl.grupanet.eakademik.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.grupanet.eakademik.authentication.AuthService;
import pl.grupanet.eakademik.user.UserProfileRepository;

@Configuration
public class UserRegistrationConfiguration {

    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;

    @Autowired
    public UserRegistrationConfiguration(UserProfileRepository userProfileRepository, AuthService authService) {
        this.userProfileRepository = userProfileRepository;
        this.authService = authService;
    }

    @Bean
    public UserRegistrationService userRegistrationService() {
        return new AppUserRegistrationService(userProfileRepository, authService);
    }
}

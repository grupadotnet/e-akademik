package pl.grupanet.eakademik.seed;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.grupanet.eakademik.register.UserRegistrationService;

@Configuration
@Profile("!prod")
public class SeedConfiguration {
    @Bean
    public StartupUsersSeeder startupUsersSeeder(UserRegistrationService userRegistrationService) {
        return new StartupUsersSeeder(userRegistrationService);
    }
}

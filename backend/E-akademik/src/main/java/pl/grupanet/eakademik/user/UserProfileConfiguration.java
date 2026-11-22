package pl.grupanet.eakademik.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserProfileConfiguration {
    @Bean
    public UserProfileRepository userProfileRepository() {
        return new AppUserProfileRepository();
    }
}

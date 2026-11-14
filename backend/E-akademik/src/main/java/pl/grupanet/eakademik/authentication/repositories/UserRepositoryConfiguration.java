package pl.grupanet.eakademik.authentication.repositories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRepositoryConfiguration {

    @Bean
    public UserRepository userRepository(SpringDataUserRepository repository) {
        return new AppUserRepository(repository);
    }
}

package pl.grupanet.eakademik.authentication.repositories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRepositoryConfiguration {

    private final SpringDataUserRepository repository;

    UserRepositoryConfiguration(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Bean
    public UserRepository userRepository() {
        return new AppUserRepository(repository);
    }
}

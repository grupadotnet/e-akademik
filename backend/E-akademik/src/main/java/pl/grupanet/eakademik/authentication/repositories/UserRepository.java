package pl.grupanet.eakademik.authentication.repositories;

import org.commons.users.UserId;
import pl.grupanet.eakademik.authentication.Email;

import java.util.Optional;

public interface UserRepository {

    RegisteredUser save(RegisteredUser user);
    Optional<RegisteredUser> findByEmail(Email email);
    Optional<RegisteredUser> findById(UserId id);
    boolean existsByEmail(Email email);
}

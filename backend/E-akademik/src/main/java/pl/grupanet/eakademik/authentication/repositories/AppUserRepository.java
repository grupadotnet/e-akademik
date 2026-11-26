package pl.grupanet.eakademik.authentication.repositories;

import lombok.RequiredArgsConstructor;
import org.commons.users.UserId;
import pl.grupanet.eakademik.authentication.Email;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
class AppUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;

    @Override
    public RegisteredUser save(RegisteredUser user) {


        UserEntity entity = UserEntity.builder()
                .id(UUID.fromString(user.id().toString()))
                .email(user.email().toString())
                .password( user.passwordHash())
                .role(user.role())
                .build();

        return toRegisteredUser(repository.save(entity));
    }

    @Override
    public Optional<RegisteredUser> findByEmail(Email email) {
        return repository.findByEmail(email.toString()).map(this::toRegisteredUser);
    }

    @Override
    public Optional<RegisteredUser> findById(UserId id) {
        return repository.findById(UUID.fromString(id.toString())).map(this::toRegisteredUser);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.toString());
    }

    private RegisteredUser toRegisteredUser(UserEntity entity) {
        return new RegisteredUser(
                new UserId(entity.getId()),
                new Email(entity.getEmail()),
                entity.getPassword(),
                entity.getRole()
        );
    }
}

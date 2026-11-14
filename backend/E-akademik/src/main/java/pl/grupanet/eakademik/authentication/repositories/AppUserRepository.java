package pl.grupanet.eakademik.authentication.repositories;

import lombok.RequiredArgsConstructor;
import org.commons.*;

import java.util.Optional;

@RequiredArgsConstructor
class AppUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;

    @Override
    public RegisteredUser save(RegisteredUser user) {

        String roomNumber = user.roomNumber() != null ? user.roomNumber().toString() : null;

        UserEntity entity = UserEntity.builder()
                .id(user.id().getId())
                .fullName(user.fullName().toString())
                .email(user.email().toString())
                .password( user.passwordHash())
                .role(user.role())
                .universityName(user.dormitoryId().getUniversityName().toString())
                .roomNumber(roomNumber)
                .dormitoryName(user.dormitoryId().getDormitoryName().toString())
                .build();

        return toRegisteredUser(repository.save(entity));
    }

    @Override
    public Optional<RegisteredUser> findByEmail(Email email) {
        return repository.findByEmail(email.toString()).map(this::toRegisteredUser);
    }

    @Override
    public Optional<RegisteredUser> findById(UserId id) {
        return repository.findById(id.getId()).map(this::toRegisteredUser);
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
                new Name(entity.getFullName()),
                new DormitoryId(new Name(entity.getUniversityName()), new Name(entity.getDormitoryName())),
                entity.getRoomNumber() != null ? new RoomNumber(entity.getRoomNumber()) : null,
                entity.getRole()
        );
    }
}

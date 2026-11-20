package pl.grupanet.eakademik.authentication.repositories;

import org.commons.Email;
import org.commons.Role;
import org.commons.users.UserId;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(UserRepositoryConfiguration.class)
class AppUserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldSaveAndFindResidentByEmailAndId() {
        UserId userId = UserId.generate();
        Email email = new Email("resident@example.com");
        RegisteredUser resident = new RegisteredUser(
                userId,
                email,
                "hashed-password",
                Role.RESIDENT
        );

        userRepository.save(resident);
        entityManager.flush();
        entityManager.clear();

        Optional<RegisteredUser> byEmail = userRepository.findByEmail(email);
        Optional<RegisteredUser> byId = userRepository.findById(userId);

        assertTrue(byEmail.isPresent());
        assertTrue(byId.isPresent());
        assertEquals(resident, byEmail.get());
        assertEquals(resident, byId.get());
    }

    @Test
    void shouldSaveAndFindStaffByEmail() {
        UserId userId = UserId.generate();
        Email email = new Email("staff@example.com");
        RegisteredUser staff = new RegisteredUser(
                userId,
                email,
                "hashed-password",
                Role.STAFF
        );

        userRepository.save(staff);
        entityManager.flush();
        entityManager.clear();

        Optional<RegisteredUser> found = userRepository.findByEmail(email);

        assertTrue(found.isPresent());
        assertEquals(staff, found.get());
        assertEquals(Role.STAFF, found.get().role());
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserDoesNotExist() {
        assertTrue(userRepository.findByEmail(new Email("missing@example.com")).isEmpty());
        assertTrue(userRepository.findById(UserId.generate()).isEmpty());
        assertFalse(userRepository.existsByEmail(new Email("missing@example.com")));
    }

    @Test
    void shouldCheckIfEmailExists() {
        Email email = new Email("exists@example.com");
        RegisteredUser user = new RegisteredUser(
                UserId.generate(),
                email,
                "hashed-password",
                Role.RESIDENT
        );

        userRepository.save(user);
        entityManager.flush();

        assertTrue(userRepository.existsByEmail(email));
    }

    @Test
    void shouldThrowExceptionWhenSavingDuplicateEmail() {
        Email email = new Email("duplicate@example.com");

        userRepository.save(new RegisteredUser(
                UserId.generate(), email, "hash1", Role.RESIDENT
        ));
        entityManager.flush();

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.save(new RegisteredUser(
                    UserId.generate(), email, "hash2", Role.RESIDENT
            ));
            entityManager.flush();
        });
    }
}

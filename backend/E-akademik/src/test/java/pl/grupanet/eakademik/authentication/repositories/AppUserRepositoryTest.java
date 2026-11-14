package pl.grupanet.eakademik.authentication.repositories;

import org.commons.DormitoryId;
import org.commons.Email;
import org.commons.Name;
import org.commons.Role;
import org.commons.RoomNumber;
import org.commons.UserId;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                new Name("Jan Kowalski"),
                new DormitoryId(new Name("Politechnika Krakowska"), new Name("DS-3 Bartek")),
                new RoomNumber("111B"),
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
    void shouldSaveAndFindStaffWithNullRoomNumber() {
        UserId userId = UserId.generate();
        Email email = new Email("staff@example.com");
        RegisteredUser staff = new RegisteredUser(
                userId,
                email,
                "hashed-password",
                new Name("Anna Nowak"),
                new DormitoryId(new Name("Politechnika Krakowska"), new Name("Ds-3 Bartek")),
                null,
                Role.STAFF
        );

        userRepository.save(staff);
        entityManager.flush();
        entityManager.clear();

        Optional<RegisteredUser> found = userRepository.findByEmail(email);

        assertTrue(found.isPresent());
        assertNull(found.get().roomNumber());
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
                new Name("Jan Kowalski"),
                new DormitoryId(new Name("Politechnika Krakowska"), new Name("DS-3 Bartek")),
                new RoomNumber("101A"),
                Role.RESIDENT
        );

        userRepository.save(user);
        entityManager.flush();

        assertTrue(userRepository.existsByEmail(email));
    }

    @Test
    void shouldThrowExceptionWhenSavingDuplicateEmail() {
        Email email = new Email("duplicate@example.com");
        DormitoryId dormitoryId = new DormitoryId(new Name("Politechnika Krakowska"), new Name("DS-3 Bartek"));

        userRepository.save(new RegisteredUser(
                UserId.generate(), email, "hash1", new Name("User One"), dormitoryId, new RoomNumber("101A"), Role.RESIDENT
        ));
        entityManager.flush();

        assertThrows(ConstraintViolationException.class, () -> {
            userRepository.save(new RegisteredUser(
                    UserId.generate(), email, "hash2", new Name("User Two"), dormitoryId, new RoomNumber("102B"), Role.RESIDENT
            ));
            entityManager.flush();
        });
    }
}

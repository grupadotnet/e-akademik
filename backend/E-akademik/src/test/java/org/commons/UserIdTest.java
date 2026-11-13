package org.commons;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIdTest {

    @Test
    void shouldReturnCorrectUuidAndStringRepresentation() {
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);

        assertEquals(uuid, userId.getId());
        assertEquals(uuid.toString(), userId.toString());
    }

    @Test
    void shouldThrowExceptionWhenUuidIsNull() {
        assertThrows(NullPointerException.class, () -> new UserId(null));
    }

    @Test
    void shouldGenerateNewUUIDCorrectly() {
        UserId id = UserId.generate();

        assertNotNull(id);
        assertNotNull(id.getId());
    }

    @Test
    void shouldGenerateNewUUIDEveryTime() {
        UserId id1 = UserId.generate();
        UserId id2 = UserId.generate();

        assertNotEquals(id1, id2);
    }

    @Test
    void shouldBeEqualWhenUUIDsAreTheSame() {
        UUID id = UUID.randomUUID();
        UserId id1 = new UserId(id);
        UserId id2 = new UserId(id);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenUUIDsAreDifferent() {
        UserId id1 = new UserId(UUID.randomUUID());
        UserId id2 = new UserId(UUID.randomUUID());

        assertNotEquals(id1, id2);
    }

    @Test
    void shouldNotBeEqualToNullOrOtherClass() {
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);

        assertNotEquals(null, userId);
        assertNotEquals(uuid, userId);
    }
}

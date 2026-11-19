package org.commons.users;

import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ResidentIdTest {

    @Test
    void residentIdShouldBeInstanceOfUserIdAndUser() {
        UUID randomUuid = UUID.randomUUID();
        ResidentId residentId = new ResidentId();

        assertInstanceOf(UserId.class, residentId);
    }
}
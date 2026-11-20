package org.commons.users;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class StaffIdTest {

    @Test
    void staffIdShouldBeInstanceOfUserIdAndUser() {
        UUID randomUuid = UUID.randomUUID();
        StaffId staffId = new StaffId(randomUuid);

        assertInstanceOf(UserId.class, staffId);
    }
}
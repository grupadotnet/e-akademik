package org.commons;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {

    @Test
    void shouldContainExpectedValuesInOrder() {
        assertThat(Role.values()).containsExactly(
                Role.RESIDENT,
                Role.STAFF,
                Role.ADMIN
        );
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void shouldReturnEnumNameInToString(Role role) {
        assertThat(role.toString()).isEqualTo(role.name());
    }

    @Test
    void shouldResolveRoleFromString() {
        assertThat(Role.valueOf("RESIDENT")).isEqualTo(Role.RESIDENT);
        assertThat(Role.valueOf("STAFF")).isEqualTo(Role.STAFF);
        assertThat(Role.valueOf("ADMIN")).isEqualTo(Role.ADMIN);
    }

    @Test
    void shouldThrowExceptionForInvalidRoleName() {
        assertThatThrownBy(() -> Role.valueOf("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
package org.commons;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordTest {

    @Test
    void shouldReturnCorrectStringRepresentationWithoutTrimming() {
        String rawPassword = " secret123 ";
        Password password = new Password(rawPassword);

        assertEquals(rawPassword, password.toString());
    }

    @Test
    void shouldAllowPasswordWithMinimumLengthOfEightCharacters() {
        Password password = new Password("12345678");

        assertEquals("12345678", password.toString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        assertThrows(NullPointerException.class, () -> new Password(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "        ", "\t\n\r     ", "short", "1234567"})
    void shouldThrowExceptionWhenPasswordIsBlankOrTooShort(String invalidPassword) {
        assertThrows(IllegalArgumentException.class, () -> new Password(invalidPassword));
    }

    @Test
    void shouldBeEqualWhenPasswordsAreSame() {
        Password password1 = new Password("secret123");
        Password password2 = new Password("secret123");

        assertEquals(password1, password2);
        assertEquals(password1.hashCode(), password2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenPasswordsAreDifferent() {
        Password password1 = new Password("secret123");
        Password password2 = new Password("secret124");

        assertNotEquals(password1, password2);
    }

    @Test
    void shouldNotBeEqualToNullOrOtherClass() {
        Password password = new Password("secret123");

        assertNotEquals(null, password);
        assertNotEquals("secret123", password);
        assertNotEquals(new Name("secret123"), password);
    }
}

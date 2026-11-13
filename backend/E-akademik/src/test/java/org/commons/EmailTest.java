package org.commons;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void shouldReturnCorrectStringRepresentation() {
        String expectedValue = "email@email.pl";

        Email email = new Email(expectedValue);

        assertEquals(expectedValue, email.toString());
        assertEquals(expectedValue, email.getEmail());
    }

    @Test
    void shouldNormalizeEmailToLowerCaseAndTrimWhitespace() {
        Email email = new Email("  Email@Email.PL  ");

        assertEquals("email@email.pl", email.toString());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "ala@kot", "a.b@c", "@.", "jan k@wp.pl", "jan@@wp.pl"})
    void shouldThrowExceptionWhenPatternDoesNotMatch(String invalidEmail) {
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    void shouldBeEqualWhenEmailsAreSameAfterNormalization() {
        Email email1 = new Email("email@email.pl");
        Email email2 = new Email("  EMAIL@Email.PL ");

        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenEmailsAreDifferent() {
        Email email1 = new Email("email@email.pl");
        Email email2 = new Email("email@email.com");

        assertNotEquals(email1, email2);
    }

    @Test
    void shouldNotBeEqualToNullOrOtherClass() {
        Email email = new Email("email@email.pl");

        assertNotEquals(null, email);
        assertNotEquals("email@email.pl", email);
    }
}

package org.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NameTest {

    @Test
    void shouldReturnCorrectStringRepresentation() {
        String expectedValue = "John";
        Name name = new Name(expectedValue);

        assertEquals(expectedValue, name.toString());
    }

    @Test
    void shouldThrowExceptionWhenInstantiatedWithNull() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    void shouldThrowExceptionWhenInstantiatedWithEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> new Name(""));
    }

    @Test
    void shouldBeEqualWhenValuesAreSame() {
        Name name1 = new Name("Alice");
        Name name2 = new Name("Alice");

        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        Name name1 = new Name("Alice");
        Name name2 = new Name("Bob");

        assertNotEquals(name1, name2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentClass() {
        Name name = new Name("Alice");

        assertNotEquals(null, name);
        assertNotEquals("Alice", name);
    }
}
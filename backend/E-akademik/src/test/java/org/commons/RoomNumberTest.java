package org.commons;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoomNumberTest {

    @Test
    void shouldReturnCorrectStringRepresentation() {
        String expectedValue = "111A";

        RoomNumber roomNumber = new RoomNumber("111A");

        assertEquals(expectedValue, roomNumber.toString());
        assertEquals(expectedValue, roomNumber.getRoomNumber());
    }

    @Test
    void shouldNormalizeRoomNumberToUpperCaseAndTrimWhitespace() {
        RoomNumber roomNumber = new RoomNumber("  308b  ");

        assertEquals("308B", roomNumber.toString());
    }

    @Test
    void shouldThrowExceptionWhenRoomNumberIsNull() {
        assertThrows(NullPointerException.class, () -> new RoomNumber(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t\n"})
    void shouldThrowExceptionWhenRoomNumberIsBlank(String blankRoomNumber) {
        assertThrows(IllegalArgumentException.class, () -> new RoomNumber(blankRoomNumber));
    }

    @Test
    void shouldBeEqualWhenRoomNumbersAreSameAfterNormalization() {
        RoomNumber room1 = new RoomNumber("111A");
        RoomNumber room2 = new RoomNumber(" 111a ");

        assertEquals(room1, room2);
        assertEquals(room1.hashCode(), room2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenRoomNumbersAreDifferent() {
        RoomNumber room1 = new RoomNumber("111A");
        RoomNumber room2 = new RoomNumber("111B");

        assertNotEquals(room1, room2);
    }

    @Test
    void shouldNotBeEqualToNullOrOtherClass() {
        RoomNumber room = new RoomNumber("111A");

        assertNotEquals(null, room);
        assertNotEquals("111A", room);
    }
}

package org.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlotNumberTest {

    @Test
    void shouldCreateSlotNumberWithPositiveValue() {
        int validNumber = 5;

        SlotNumber slotNumber = new SlotNumber(validNumber);

        assertEquals(validNumber, slotNumber.getNumberAsInt());
    }

    @Test
    void shouldCreateSlotNumberWithZero() {
        int zero = 0;

        SlotNumber slotNumber = new SlotNumber(zero);

        assertEquals(zero, slotNumber.getNumberAsInt());
    }

    @Test
    void shouldThrowExceptionWhenNumberIsNegative() {
        int negativeNumber = -1;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SlotNumber(negativeNumber)
        );

        assertEquals("Number must be positive", exception.getMessage());
    }

    @Test
    void shouldReturnCorrectStringRepresentation() {
        SlotNumber slotNumber = new SlotNumber(42);

        String stringValue = slotNumber.toString();

        assertEquals("42", stringValue);
    }

    @Test
    void shouldBeEqualToItself() {
        SlotNumber slotNumber = new SlotNumber(5);

        assertEquals(slotNumber, slotNumber);
        assertEquals(slotNumber.hashCode(), slotNumber.hashCode());
    }

    @Test
    void shouldBeEqualWhenNumbersAreIdentical() {
        SlotNumber first = new SlotNumber(5);
        SlotNumber second = new SlotNumber(5);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenNumbersDiffer() {
        SlotNumber first = new SlotNumber(5);
        SlotNumber second = new SlotNumber(6);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        SlotNumber slotNumber = new SlotNumber(5);

        assertNotEquals(null, slotNumber);
        assertNotEquals(5, slotNumber);
        assertNotEquals("5", slotNumber);
    }
}
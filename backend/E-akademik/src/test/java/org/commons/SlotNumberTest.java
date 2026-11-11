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
}
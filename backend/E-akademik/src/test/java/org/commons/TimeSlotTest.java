package org.commons;

import org.junit.jupiter.api.Test;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TimeSlotTest {

    @Test
    void shouldCreateTimeSlotWhenStartTimeIsBeforeEndTime() {
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(9, 30);

        TimeSlot slot = assertDoesNotThrow(() -> new TimeSlot(start, end));

        assertEquals(start, slot.startTime());
        assertEquals(end, slot.endTime());
    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsAfterEndTime() {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(9, 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TimeSlot(start, end)
        );

        assertEquals("Start time must be before end time", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartTimeEqualsEndTime() {
        // Given
        LocalTime time = LocalTime.of(12, 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new TimeSlot(time, time)
        );

        assertEquals("Start time must be before end time", exception.getMessage());
    }
}
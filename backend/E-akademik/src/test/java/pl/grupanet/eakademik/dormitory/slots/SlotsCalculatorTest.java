package pl.grupanet.eakademik.dormitory.slots;

import org.commons.SlotNumber;
import org.commons.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SlotsCalculatorTest {

    @Test
    void shouldGenerateDefaultSlotCalculator() {
        SlotsCalculator slotsCalculator = new SlotsCalculator();

        assertTrue(slotsCalculator.generateSlotIds().isEmpty());
        assertNull(slotsCalculator.calculateSlotHoursById(new SlotNumber(0)));
    }

    @Test
    void shouldGenerateSlotIdsStartingFromZero() {
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(12, 0));
        Duration slotDuration = Duration.ofMinutes(90);
        Duration breakDuration = Duration.ofMinutes(15);
        SlotsCalculator calculator = new SlotsCalculator(timeSlot, slotDuration, breakDuration);

        List<SlotNumber> slotIds = calculator.generateSlotIds();

        assertEquals(2, slotIds.size());

        assertEquals(0, slotIds.get(0).getNumberAsInt());
        assertEquals(1, slotIds.get(1).getNumberAsInt());
    }

    @Test
    void shouldReturnEmptyListWhenTotalDurationIsZeroOrNegative() {
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(10, 0), LocalTime.of(10, 10));
        Duration slotDuration = Duration.ofMinutes(90);
        Duration breakDuration = Duration.ofMinutes(15);
        SlotsCalculator calculator = new SlotsCalculator(timeSlot, slotDuration, breakDuration);

        List<SlotNumber> slotIds = calculator.generateSlotIds();

        assertTrue(slotIds.isEmpty());
    }

    @Test
    void shouldCalculateCorrectSlotHoursForIndexOne() {
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(12, 0));
        Duration slotDuration = Duration.ofMinutes(90);
        Duration breakDuration = Duration.ofMinutes(15);
        SlotsCalculator calculator = new SlotsCalculator(timeSlot, slotDuration, breakDuration);

        SlotNumber slotNumber = new SlotNumber(0);
        TimeSlot calculatedSlot = calculator.calculateSlotHoursById(slotNumber);

        assertEquals(LocalTime.of(8, 0), calculatedSlot.startTime());
        assertEquals(LocalTime.of(9, 30), calculatedSlot.endTime());
    }

    @Test
    void shouldCalculateCorrectSlotHoursForIndexTwo() {
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(12, 0));
        Duration slotDuration = Duration.ofMinutes(90);
        Duration breakDuration = Duration.ofMinutes(15);
        SlotsCalculator calculator = new SlotsCalculator(timeSlot, slotDuration, breakDuration);

        SlotNumber slotNumber = new SlotNumber(1);
        TimeSlot calculatedSlot = calculator.calculateSlotHoursById(slotNumber);

        assertEquals(LocalTime.of(9, 45), calculatedSlot.startTime());
        assertEquals(LocalTime.of(11, 15), calculatedSlot.endTime());
    }

    @Test
    void shouldThrowExceptionWhenConstructedWithNullArguments() {
        assertThrows(NullPointerException.class, () -> {
            new SlotsCalculator(null, Duration.ofMinutes(90), Duration.ofMinutes(15));
        });

        assertThrows(NullPointerException.class, () -> {
            new SlotsCalculator(new TimeSlot(LocalTime.of(8, 0), LocalTime.of(12, 0)), null, Duration.ofMinutes(15));
        });

        assertThrows(NullPointerException.class, () -> {
            new SlotsCalculator(new TimeSlot(LocalTime.of(8, 0), LocalTime.of(12, 0)), Duration.ofMinutes(90), null);
        });
    }

    @Test
    void shouldBeEqualWhenInstancesHaveSameValues() {
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(16, 0));
        Duration slotDuration = Duration.ofMinutes(90);
        Duration breakDuration = Duration.ofMinutes(15);

        SlotsCalculator calc1 = new SlotsCalculator(timeSlot, slotDuration, breakDuration);
        SlotsCalculator calc2 = new SlotsCalculator(timeSlot, slotDuration, breakDuration);

        assertEquals(calc1, calc2);
    }

    @Test
    void shouldNotBeEqualWhenInstancesHaveDifferentValues() {
        TimeSlot timeSlot1 = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(16, 0));
        TimeSlot timeSlot2 = new TimeSlot(LocalTime.of(9, 0), LocalTime.of(17, 0));
        Duration slotDuration1 = Duration.ofMinutes(90);
        Duration slotDuration2 = Duration.ofMinutes(60);
        Duration breakDuration1 = Duration.ofMinutes(15);
        Duration breakDuration2 = Duration.ofMinutes(10);

        SlotsCalculator baseCalc = new SlotsCalculator(timeSlot1, slotDuration1, breakDuration1);
        SlotsCalculator diffTimeSlotCalc = new SlotsCalculator(timeSlot2, slotDuration1, breakDuration1);
        SlotsCalculator diffSlotDurationCalc = new SlotsCalculator(timeSlot1, slotDuration2, breakDuration1);
        SlotsCalculator diffBreakDurationCalc = new SlotsCalculator(timeSlot1, slotDuration1, breakDuration2);

        assertNotEquals(baseCalc, diffTimeSlotCalc);
        assertNotEquals(baseCalc, diffSlotDurationCalc);
        assertNotEquals(baseCalc, diffBreakDurationCalc);
    }

    @Test
    void shouldNotBeEqualWhenComparingWithNull() {
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(16, 0));
        SlotsCalculator calc = new SlotsCalculator(timeSlot, Duration.ofMinutes(90), Duration.ofMinutes(15));

        assertNotEquals(null, calc);
    }

    @Test
    void shouldNotBeEqualWhenComparingWithDifferentType() {
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(16, 0));
        SlotsCalculator calc = new SlotsCalculator(timeSlot, Duration.ofMinutes(90), Duration.ofMinutes(15));
        Object otherObject = new Object();

        assertNotEquals(calc, otherObject);
    }

    @Test
    void shouldBeEqualWithItself() {
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(16, 0));
        SlotsCalculator calc = new SlotsCalculator(timeSlot, Duration.ofMinutes(90), Duration.ofMinutes(15));

        assertEquals(calc, calc);
    }

    @Test
    void shouldHaveSameHashCodeWhenInstancesAreEquals() {
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(16, 0));
        Duration slotDuration = Duration.ofMinutes(90);
        Duration breakDuration = Duration.ofMinutes(15);

        SlotsCalculator calc1 = new SlotsCalculator(timeSlot, slotDuration, breakDuration);
        SlotsCalculator calc2 = new SlotsCalculator(timeSlot, slotDuration, breakDuration);

        assertEquals(calc1.hashCode(), calc2.hashCode());
    }

    @Test
    void shouldHaveDifferentHashCodeWhenInstancesAreDifferent() {
        TimeSlot timeSlot1 = new TimeSlot(LocalTime.of(8, 0), LocalTime.of(16, 0));
        TimeSlot timeSlot2 = new TimeSlot(LocalTime.of(9, 0), LocalTime.of(17, 0));
        Duration slotDuration = Duration.ofMinutes(90);
        Duration breakDuration = Duration.ofMinutes(15);

        SlotsCalculator calc1 = new SlotsCalculator(timeSlot1, slotDuration, breakDuration);
        SlotsCalculator calc2 = new SlotsCalculator(timeSlot2, slotDuration, breakDuration);

        assertNotEquals(calc1.hashCode(), calc2.hashCode());
    }
}
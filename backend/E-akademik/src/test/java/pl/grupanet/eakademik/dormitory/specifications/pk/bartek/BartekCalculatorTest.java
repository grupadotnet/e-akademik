package pl.grupanet.eakademik.dormitory.specifications.pk.bartek;

import org.commons.SlotNumber;
import org.commons.time.TimeSlot;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekCalculator;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BartekCalculatorTest {

    @Test
    void shouldGenerateEightTwoHourSlotsBetween7And23() {
        BartekCalculator calculator = new BartekCalculator();
        List<SlotNumber> slotIds = calculator.generateSlotIds();

        assertEquals(8, slotIds.size());
        for (int i = 0; i < 8; i++) {
            assertEquals(i, slotIds.get(i).getNumberAsInt());
        }
    }

    @Test
    void shouldCalculateCorrectTimeSlotsForAllSlotIds() {
        BartekCalculator calculator = new BartekCalculator();

        List<TimeSlot> expectedTimeSlots = List.of(
                new TimeSlot(LocalTime.of(7, 0), LocalTime.of(9, 0)),
                new TimeSlot(LocalTime.of(9, 0), LocalTime.of(11, 0)),
                new TimeSlot(LocalTime.of(11, 0), LocalTime.of(13, 0)),
                new TimeSlot(LocalTime.of(13, 0), LocalTime.of(15, 0)),
                new TimeSlot(LocalTime.of(15, 0), LocalTime.of(17, 0)),
                new TimeSlot(LocalTime.of(17, 0), LocalTime.of(19, 0)),
                new TimeSlot(LocalTime.of(19, 0), LocalTime.of(21, 0)),
                new TimeSlot(LocalTime.of(21, 0), LocalTime.of(23, 0))
        );

        for (int i = 0; i < expectedTimeSlots.size(); i++) {
            TimeSlot actualSlot = calculator.calculateSlotHoursById(new SlotNumber(i));
            assertEquals(expectedTimeSlots.get(i), actualSlot);
        }
    }
}
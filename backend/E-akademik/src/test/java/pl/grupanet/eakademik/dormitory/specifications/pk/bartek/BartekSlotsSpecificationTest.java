package pl.grupanet.eakademik.dormitory.specifications.pk.bartek;

import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekSlotsSpecification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BartekSlotsSpecificationTest {

    @Test
    void shouldCalculateEightSlotsBetween7And23() {
        ChooseSlotsCalculator calculator = BartekSlotsSpecification.createCalculator();

        assertEquals(8, calculator.choose(LocalDateTime.now()).generateSlotIds().size());
    }
}
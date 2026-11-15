package pl.grupanet.eakademik.dormitory.specifications.pk.bartek;

import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.bartek.BartekCalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BartekCalculatorTest {

    @Test
    void shouldGenerateEightTwoHourSlotsBetween7And23() {
        BartekCalculator calculator = new BartekCalculator();

        assertEquals(8, calculator.generateSlotIds().size());
    }
}
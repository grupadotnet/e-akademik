package pl.grupanet.eakademik.dormitory.spec.pk.bartek;

import org.commons.time.TimeSlot;
import pl.grupanet.eakademik.dormitory.slots.SlotsCalculator;

import java.time.Duration;
import java.time.LocalTime;

public class BartekCalculator extends SlotsCalculator {
    public BartekCalculator() {
        super(
                new TimeSlot(LocalTime.of(7, 0), LocalTime.of(23, 0)),
                Duration.ofHours(2),
                Duration.ZERO
        );
    }
}
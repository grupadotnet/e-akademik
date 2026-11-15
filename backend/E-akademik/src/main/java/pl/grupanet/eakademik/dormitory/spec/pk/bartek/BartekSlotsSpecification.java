package pl.grupanet.eakademik.dormitory.spec.pk.bartek;

import org.commons.TimeSlot;
import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;

import java.time.Duration;
import java.time.LocalTime;

public class BartekSlotsSpecification {

    public static final ChooseSlotsCalculator BARTEK_CALCULATOR = createCalculator();
    public static final SlotsRoomSpecification BARTEK_LAUNDRY_ROOMS = create();

    public static ChooseSlotsCalculator createCalculator() {
        return new ChooseSlotsCalculator.Builder(
                new SlotsCalculator(
                        new TimeSlot(LocalTime.of(7, 0), LocalTime.of(23, 0)),
                        Duration.ofHours(2),
                        Duration.ZERO
                )
        ).build();
    }

    public static SlotsRoomSpecification create() {
        return new SlotsRoomSpecification.Builder()
                .add("211", BARTEK_CALCULATOR)
                .add("411", BARTEK_CALCULATOR)
                .add("611", BARTEK_CALCULATOR)
                .add("811", BARTEK_CALCULATOR)
                .add("1011", BARTEK_CALCULATOR)
                .build();
    }
}
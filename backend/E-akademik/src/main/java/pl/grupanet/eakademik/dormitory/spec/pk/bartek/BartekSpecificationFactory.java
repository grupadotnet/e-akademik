package pl.grupanet.eakademik.dormitory.spec.pk.bartek;

import org.commons.TimeSlot;
import pl.grupanet.eakademik.dormitory.DormitorySpecificationFactory;
import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
import pl.grupanet.eakademik.dormitory.spec.pk.PKContains;

import java.time.Duration;
import java.time.LocalTime;

public class BartekSpecificationFactory extends DormitorySpecificationFactory {

    public BartekSpecificationFactory() {
        super(PKContains.POLITECHNIKA_KRAKOWSKA.getUniversityName(), "Ds-3 Bartek");
    }

    @Override
    public SlotsRoomSpecification buildSlotsRoomSpecification() {
        ChooseSlotsCalculator bartekCalculator = new ChooseSlotsCalculator.Builder(
                new SlotsCalculator(
                        new TimeSlot(LocalTime.of(7, 0), LocalTime.of(23, 0)),
                        Duration.ofHours(2),
                        Duration.ZERO
                )
        ).build();

        return new SlotsRoomSpecification.Builder()
                .add("211", bartekCalculator)
                .add("411", bartekCalculator)
                .add("611", bartekCalculator)
                .add("811", bartekCalculator)
                .add("1011", bartekCalculator)
                .build();
    }
}
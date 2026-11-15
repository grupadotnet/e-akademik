package pl.grupanet.eakademik.dormitory.spec.pk.bartek;

import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;

public class BartekLaundryRoomFactory {

    public SlotsRoomSpecification create() {
        ChooseSlotsCalculator calculator = createCalculator();

        return new SlotsRoomSpecification.Builder()
                .add("211", calculator)
                .add("411", calculator)
                .add("611", calculator)
                .add("811", calculator)
                .add("1011", calculator)
                .build();
    }

    private ChooseSlotsCalculator createCalculator() {
        return new ChooseSlotsCalculator.Builder(new BartekCalculator()).build();
    }
}
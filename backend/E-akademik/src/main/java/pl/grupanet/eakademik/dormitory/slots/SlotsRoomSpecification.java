package pl.grupanet.eakademik.dormitory.slots;

import lombok.NonNull;
import org.commons.Name;

import java.util.HashMap;

public class SlotsRoomSpecification {
    public static SlotsRoomSpecification defaultRoomSpecification = new Builder().
            add("room 1", ChooseSlotsCalculator.defaultChooseCalculator).build();

    public static class Builder {
        private final HashMap<Name, ChooseSlotsCalculator> calculatorsByRoomName = new HashMap<>();

        public Builder add(String name, ChooseSlotsCalculator calculator) {
            this.calculatorsByRoomName.put(new Name(name), calculator);
            return this;
        }

        public SlotsRoomSpecification build() {
            return new SlotsRoomSpecification(calculatorsByRoomName);
        }
    }

    private final HashMap<Name, ChooseSlotsCalculator> calculatorsByRoomName;

    private SlotsRoomSpecification(@NonNull HashMap<Name, ChooseSlotsCalculator> calculatorsByRoomName) {
        if (calculatorsByRoomName.isEmpty()) throw new IllegalArgumentException("Rooms cannot be without slots");

        this.calculatorsByRoomName = calculatorsByRoomName;
    }

    public ChooseSlotsCalculator getCalculatorByRoomName(@NonNull Name roomName) {
        return calculatorsByRoomName.get(roomName);
    }

}

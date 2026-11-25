package pl.grupanet.eakademik.dormitory.slots;

import lombok.NonNull;
import org.commons.RoomNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SlotsRoomSpecification {
    public static SlotsRoomSpecification defaultRoomSpecification = new Builder().
            add("room 1", ChooseSlotsCalculator.defaultChooseCalculator).build();

    public static class Builder {
        private final HashMap<RoomNumber, ChooseSlotsCalculator> calculatorsByRoomName = new HashMap<>();

        public Builder add(String name, ChooseSlotsCalculator calculator) {
            this.calculatorsByRoomName.put(new RoomNumber(name), calculator);
            return this;
        }

        public SlotsRoomSpecification build() {
            return new SlotsRoomSpecification(calculatorsByRoomName);
        }
    }

    private final HashMap<RoomNumber, ChooseSlotsCalculator> calculatorsByRoomName;

    private SlotsRoomSpecification(@NonNull HashMap<RoomNumber, ChooseSlotsCalculator> calculatorsByRoomName) {
        if (calculatorsByRoomName.isEmpty()) throw new IllegalArgumentException("Rooms cannot be without slots");

        this.calculatorsByRoomName = calculatorsByRoomName;
    }

    public ChooseSlotsCalculator getCalculatorByRoomName(@NonNull RoomNumber roomName) {
        return calculatorsByRoomName.get(roomName);
    }

    public List<RoomNumber> getRooms() {
        return new ArrayList<>(calculatorsByRoomName.keySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotsRoomSpecification that = (SlotsRoomSpecification) o;
        return Objects.equals(calculatorsByRoomName, that.calculatorsByRoomName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(calculatorsByRoomName);
    }

}

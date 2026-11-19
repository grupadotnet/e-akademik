package pl.grupanet.eakademik.reservation;

import lombok.NonNull;
import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;

import java.util.Objects;

public record ReservationId(@NonNull DormitoryId dormitoryId, @NonNull RoomNumber roomNumber,
                     @NonNull SlotNumber slotNumber) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationId that = (ReservationId) o;
        return Objects.equals(roomNumber, that.roomNumber) && Objects.equals(slotNumber, that.slotNumber) && Objects.equals(dormitoryId, that.dormitoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dormitoryId, roomNumber, slotNumber);
    }
}

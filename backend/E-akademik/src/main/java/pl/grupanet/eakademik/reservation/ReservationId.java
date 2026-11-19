package pl.grupanet.eakademik.reservation;

import lombok.NonNull;
import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;

record ReservationId(@NonNull DormitoryId dormitoryId, @NonNull RoomNumber roomNumber,
                     @NonNull SlotNumber slotNumber) {
}

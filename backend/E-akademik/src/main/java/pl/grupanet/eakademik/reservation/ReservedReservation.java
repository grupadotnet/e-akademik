package pl.grupanet.eakademik.reservation;

import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;

class ReservedReservation extends Reservation {
    private final ResidentId ownerId;

    public ReservedReservation(ReservationId reservationId, @NonNull ResidentId residentId) {
        super(reservationId);

        this.ownerId = residentId;
    }

    public OpenReservation cancel(@NonNull ResidentId residentId) { //cancel by owner
        if (!ownerId.equals(residentId)) throw new IllegalArgumentException("Only owners can cancel");

        return new OpenReservation(reservationId);
    }

    public AcceptedReservation accept(@NonNull StaffId staffId) {
        return new AcceptedReservation(reservationId, ownerId, staffId);
    }
}

package pl.grupanet.eakademik.reservation;

import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;

class AcceptedReservation extends Reservation {
    private final ResidentId ownerId;
    private final StaffId acceptedId;

    public AcceptedReservation(ReservationId reservationId, @NonNull ResidentId residentId, StaffId acceptedId) {
        super(reservationId);

        this.ownerId = residentId;
        this.acceptedId = acceptedId;
    }



    public OpenReservation cancel(@NonNull ResidentId residentId) { //cancel by owner
        if (!ownerId.equals(residentId)) throw new IllegalArgumentException("Only owners can cancel");

        return new OpenReservation(reservationId);
    }

    public OpenReservation cancel(@NonNull StaffId staffId) { //cancel by staff member
        return new OpenReservation(reservationId);
    }
}

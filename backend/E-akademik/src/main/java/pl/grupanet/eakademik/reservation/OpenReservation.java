package pl.grupanet.eakademik.reservation;

import lombok.NonNull;
import org.commons.users.ResidentId;

class OpenReservation extends Reservation {
    public OpenReservation(ReservationId reservationId) {
        super(reservationId);
    }

    public ReservedReservation reserve(@NonNull ResidentId residentId) {
        return new ReservedReservation(reservationId, residentId);
    }
}

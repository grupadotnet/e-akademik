package pl.grupanet.eakademik.reservation;

import lombok.NonNull;
import org.commons.users.ResidentId;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;

class OpenTerm extends Term {
    public OpenTerm(ReservationId reservationId) {
        super(reservationId, ReservationStatus.OPEN);
    }

    public ReservedTerm reserve(@NonNull ResidentId residentId) {
        return new ReservedTerm(getReservationId(), residentId);
    }

    @Override
    protected TermSnapshot toDto() {
        return new TermSnapshot(
                getReservationId(),
                getStatus(),
                null,
                null
        );
    }
}

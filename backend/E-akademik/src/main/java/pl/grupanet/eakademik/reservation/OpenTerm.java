package pl.grupanet.eakademik.reservation;

import lombok.NonNull;
import org.commons.users.ResidentId;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;

import java.time.LocalDate;

class OpenTerm extends Term {
    public OpenTerm(ReservationId reservationId, LocalDate localDate) {
        super(reservationId, ReservationStatus.OPEN, localDate);
    }

    public ReservedTerm reserve(@NonNull ResidentId residentId) {
        return new ReservedTerm(getReservationId(), residentId);
    }

    @Override
    protected TermSnapshot toDto() {
        return new TermSnapshot(
                getReservationId(),
                getStatus(),
                getLocalDate(),
                null,
                null
        );
    }
}

package pl.grupanet.eakademik.reservation;

import lombok.Getter;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;

@Getter
abstract class Term {
    private final ReservationId reservationId;
    private final ReservationStatus status;

    public Term(final ReservationId reservationId, ReservationStatus status) {
        this.reservationId = reservationId;
        this.status = status;
    }

    protected abstract TermSnapshot toDto();
}
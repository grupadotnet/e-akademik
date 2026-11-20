package pl.grupanet.eakademik.reservation;

import lombok.Getter;
import lombok.NonNull;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;

import java.time.LocalDate;

@Getter
abstract class Term {
    private final ReservationId reservationId;
    private final ReservationStatus status;
    private final LocalDate localDate;

    public Term(@NonNull final ReservationId reservationId, @NonNull final ReservationStatus status,
                @NonNull final LocalDate localDate) {
        this.reservationId = reservationId;
        this.status = status;
        this.localDate = localDate;
    }

    protected abstract TermSnapshot toDto();
}
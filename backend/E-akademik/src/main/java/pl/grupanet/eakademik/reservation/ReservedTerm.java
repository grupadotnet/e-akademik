package pl.grupanet.eakademik.reservation;

import lombok.Getter;
import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;

import java.time.LocalDate;

@Getter
class ReservedTerm extends Term {
    private final ResidentId ownerId;

    public ReservedTerm(@NonNull final ReservationId reservationId, @NonNull final ResidentId residentId,
                        @NonNull final LocalDate localDate) {
        super(reservationId, ReservationStatus.RESERVED, localDate);

        this.ownerId = residentId;
    }

    public OpenTerm cancel(@NonNull ResidentId residentId) { //cancel by owner
        if (!ownerId.equals(residentId)) throw new IllegalArgumentException("Only owners can cancel");

        return new OpenTerm(getReservationId(), getLocalDate());
    }

    public AcceptedTerm accept(@NonNull StaffId staffId) {
        return new AcceptedTerm(getReservationId(), ownerId, getLocalDate(), staffId);
    }



    @Override
    protected TermSnapshot toDto() {
        return new TermSnapshot(
                getReservationId(),
                getStatus(),
                getLocalDate(),
                getOwnerId(),
                null
        );
    }
}

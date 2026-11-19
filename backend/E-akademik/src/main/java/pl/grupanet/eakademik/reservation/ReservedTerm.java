package pl.grupanet.eakademik.reservation;

import lombok.Getter;
import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;

@Getter
class ReservedTerm extends Term {
    private final ResidentId ownerId;

    public ReservedTerm(ReservationId reservationId, @NonNull ResidentId residentId) {
        super(reservationId, ReservationStatus.RESERVED);

        this.ownerId = residentId;
    }

    public OpenTerm cancel(@NonNull ResidentId residentId) { //cancel by owner
        if (!ownerId.equals(residentId)) throw new IllegalArgumentException("Only owners can cancel");

        return new OpenTerm(getReservationId());
    }

    public AcceptedTerm accept(@NonNull StaffId staffId) {
        return new AcceptedTerm(getReservationId(), ownerId, staffId);
    }



    @Override
    protected TermSnapshot toDto() {
        return new TermSnapshot(
                getReservationId(),
                getStatus(),
                getOwnerId(),
                null
        );
    }
}

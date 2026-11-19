package pl.grupanet.eakademik.reservation;

import lombok.Getter;
import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;

@Getter
class AcceptedTerm extends Term {
    private final ResidentId ownerId;
    private final StaffId acceptedId;

    public AcceptedTerm(ReservationId reservationId, @NonNull ResidentId residentId, StaffId acceptedId) {
        super(reservationId, ReservationStatus.ACCEPTED);

        this.ownerId = residentId;
        this.acceptedId = acceptedId;
    }


    public OpenTerm cancel(@NonNull ResidentId residentId) { //cancel by owner
        if (!ownerId.equals(residentId)) throw new IllegalArgumentException("Only owners can cancel");

        return new OpenTerm(getReservationId());
    }

    public OpenTerm cancel(@NonNull StaffId staffId) { //cancel by staff member
        return new OpenTerm(getReservationId());
    }

    @Override
    protected TermSnapshot toDto() {
        return new TermSnapshot(
                getReservationId(),
                getStatus(),
                getOwnerId(),
                getAcceptedId()
        );
    }
}

package pl.grupanet.eakademik.reservation;

import lombok.Getter;
import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;

import java.time.LocalDate;

@Getter
class AcceptedTerm extends Term {
    private final ResidentId ownerId;
    private final StaffId acceptedId;

    public AcceptedTerm(@NonNull ReservationId reservationId, @NonNull ResidentId residentId,
                        @NonNull LocalDate localDate, @NonNull StaffId acceptedId) {
        super(reservationId, ReservationStatus.ACCEPTED, localDate);

        this.ownerId = residentId;
        this.acceptedId = acceptedId;
    }


    public OpenTerm cancel(@NonNull ResidentId residentId) { //cancel by owner
        if (!ownerId.equals(residentId)) throw new IllegalArgumentException("Only owners can cancel");

        return new OpenTerm(getReservationId(), getLocalDate());
    }

    public OpenTerm cancel(@NonNull StaffId staffId) { //cancel by staff member
        return new OpenTerm(getReservationId(), getLocalDate());
    }

    @Override
    protected TermSnapshot toDto() {
        return new TermSnapshot(
                getReservationId(),
                getStatus(),
                getLocalDate(),
                getOwnerId(),
                getAcceptedId()
        );
    }
}

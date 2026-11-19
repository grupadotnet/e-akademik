package pl.grupanet.eakademik.reservation.dto;

import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.reservation.ReservationId;

import java.util.Objects;

public record TermSnapshot(
        @NonNull ReservationId id,
        @NonNull ReservationStatus status,
        ResidentId ownerId,
        StaffId acceptedById
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermSnapshot that = (TermSnapshot) o;
        return Objects.equals(id, that.id) && Objects.equals(ownerId, that.ownerId) && Objects.equals(acceptedById, that.acceptedById) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, ownerId, acceptedById);
    }
}

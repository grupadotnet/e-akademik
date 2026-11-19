package pl.grupanet.eakademik.reservation;

import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;

import java.time.LocalDate;
import java.util.List;

interface ReservationRepository {
    List<TermSnapshot> findAllTermsForSlotAndDate(List<ReservationStatus> statuses, List<LocalDate> dates);
    TermSnapshot reserve(@NonNull final ReservationId id,
                         @NonNull final ResidentId residentId,
                         @NonNull final LocalDate reservationDate);
    TermSnapshot accept(@NonNull final ReservationId id,
                        @NonNull final ResidentId residentId,
                        @NonNull final StaffId staffId,
                        @NonNull final LocalDate reservationDate);
    TermSnapshot cancelByStaff(@NonNull final ReservationId id,
                               @NonNull final ResidentId residentId,
                               @NonNull final StaffId staffId,
                               @NonNull final LocalDate reservationDate);
    TermSnapshot cancelByOwner(@NonNull final ReservationId id,
                               @NonNull final ResidentId residentId,
                               @NonNull final LocalDate reservationDate);

}

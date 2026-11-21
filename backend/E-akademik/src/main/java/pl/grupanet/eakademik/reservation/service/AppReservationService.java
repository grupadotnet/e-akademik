package pl.grupanet.eakademik.reservation.service;

import java.time.LocalDate;
import java.util.UUID;

public class AppReservationService {
    void reserve(UUID residentId, LocalDate reservationDate, int slotNumber){

    }

    /*
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
     */
}

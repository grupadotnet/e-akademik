package pl.grupanet.eakademik.reservation.services.laundry;

import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.services.dto.ReservationByDateDTO;
import pl.grupanet.eakademik.reservation.services.dto.ReservationByRoomsDTO;
import pl.grupanet.eakademik.reservation.services.dto.ReservationDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LaundryReservationService {
    ReservationDTO reserve(UUID residentUUID, LocalDateTime reservationDate,
                           String roomToReserveId, int slotNumberId);
    ReservationDTO accept(UUID residentUUID, UUID staffUUID, LocalDateTime reservationDate,
                          String roomToReserveId, int slotNumberId);
    ReservationDTO cancelByStaff(UUID residentUUID, UUID staffUUID, LocalDateTime reservationDate,
                                 String roomToReserveId, int slotNumberId);
    ReservationDTO cancelByOwner(UUID residentUUID, LocalDateTime reservationDate,
                                 String roomToReserveId, int slotNumberId);
    List<ReservationDTO> findReservations(UUID finderUUID, LocalDateTime reservationDate,
                                          String roomToReserveId, List<ReservationStatus> statuses);
    ReservationByRoomsDTO findReservations(UUID finderUUID, LocalDateTime reservationDate,
                                           List<ReservationStatus> statuses);
    ReservationByDateDTO findReservations(UUID finderUUID, List<LocalDateTime> reservationDates,
                                          List<ReservationStatus> statuses);
}

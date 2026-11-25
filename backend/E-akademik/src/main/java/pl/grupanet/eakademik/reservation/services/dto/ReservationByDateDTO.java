package pl.grupanet.eakademik.reservation.services.dto;

import java.time.LocalDate;
import java.util.HashMap;

public record ReservationByDateDTO(HashMap<LocalDate, ReservationByRoomsDTO> dates) {
}

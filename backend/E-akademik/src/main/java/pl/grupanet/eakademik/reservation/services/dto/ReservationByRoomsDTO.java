package pl.grupanet.eakademik.reservation.services.dto;

import java.util.HashMap;
import java.util.List;

public record ReservationByRoomsDTO(HashMap<String, List<ReservationDTO>> rooms) {
}

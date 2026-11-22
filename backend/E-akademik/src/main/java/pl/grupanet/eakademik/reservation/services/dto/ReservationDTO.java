package pl.grupanet.eakademik.reservation.services.dto;

import org.commons.time.TimeSlot;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.user.dto.ResidentIdDto;
import pl.grupanet.eakademik.user.dto.StaffIdDto;

public record ReservationDTO(String reservedRoomNumber, TimeSlot timeSlot, int slotNumber,
                             ReservationStatus status, ResidentIdDto reservedBy, StaffIdDto approvedBy) {
}

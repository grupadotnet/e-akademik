package pl.grupanet.eakademik.reservation.services.laundry;


import org.commons.time.TimeSlot;
import pl.grupanet.eakademik.reservation.ReservationId;
import pl.grupanet.eakademik.user.dto.ResidentDto;

record ReservationContextDto(ResidentDto residentDto, ReservationId reservationId, TimeSlot timeSlot) {

}

package pl.grupanet.eakademik.reservation;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.users.ResidentId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OpenTermTest {

    private static ReservationId createReservationId() {
        return new ReservationId(
                new DormitoryId.Builder(
                        UniversityNames.POLITECHNIKA_KRAKOWSKA, "sth")
                        .build(),
                new RoomNumber("A001"),
                new SlotNumber(0));
    }

    @Test
    void shouldBuildReservation() {
        ReservationId reservationId = createReservationId();
        OpenTerm acceptedReservation = new OpenTerm(reservationId, getDummyDate());

        assertInstanceOf(Term.class, acceptedReservation);
    }

    @Test
    void shouldCorrectCreateSnapshot() {
        ReservationId reservationId = createReservationId();
        OpenTerm acceptedReservation = new OpenTerm(reservationId, getDummyDate());

        TermSnapshot reservationSnapshot = acceptedReservation.toDto();
        TermSnapshot expecedReservationSnapshot = new TermSnapshot(
                reservationId, ReservationStatus.OPEN, getDummyDate(), null, null
        );
        assertEquals(reservationSnapshot, expecedReservationSnapshot);
    }

    @Test
    void shouldSuccessfullyReserveAndReturnReservedReservation() {
        ReservationId reservationId = createReservationId();
        OpenTerm openReservation = new OpenTerm(reservationId, getDummyDate());

        ResidentId residentId = new ResidentId(UUID.randomUUID());

        ReservedTerm result = openReservation.reserve(residentId);

        assertNotNull(result);
        assertInstanceOf(ReservedTerm.class, result);
    }

    @Test
    void shouldThrowExceptionWhenResidentIdIsNull() {
        ReservationId reservationId = createReservationId();
        OpenTerm openReservation = new OpenTerm(reservationId, getDummyDate());

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> openReservation.reserve(null)
        );
    }

    private static LocalDate getDummyDate() {
        return LocalDate.now();
    }
}
package pl.grupanet.eakademik.reservation;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.users.ResidentId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OpenReservationTest {

    private static ReservationId createReservationId() {
        return new ReservationId(
                new DormitoryId.Builder(
                    UniversityNames.POLITECHNIKA_KRAKOWSKA,"sth")
                        .build(),
                new RoomNumber("A001"),
                new SlotNumber(0));
    }

    @Test
    void shouldSuccessfullyReserveAndReturnReservedReservation() {
        ReservationId reservationId = createReservationId();
        OpenReservation openReservation = new OpenReservation(reservationId);

        ResidentId residentId = new ResidentId(UUID.randomUUID());

        ReservedReservation result = openReservation.reserve(residentId);

        assertNotNull(result, "Zwrócona rezerwacja nie powinna być nullem");
        assertInstanceOf(ReservedReservation.class, result, "Wynik powinien być typu ReservedReservation");
    }

    @Test
    void shouldThrowExceptionWhenResidentIdIsNull() {
        ReservationId reservationId = createReservationId();
        OpenReservation openReservation = new OpenReservation(reservationId);

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> openReservation.reserve(null)
        );

        assertTrue(exception.getMessage().contains("residentId is marked non-null but is null"),
                "Komunikat wyjątku powinien wskazywać na brakujący residentId");
    }
}
package pl.grupanet.eakademik.reservation;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservedReservationTest {
    private ReservationId createDummyReservationId() {
        return new ReservationId(
                new DormitoryId.Builder(
                        UniversityNames.POLITECHNIKA_KRAKOWSKA, "sth")
                        .build(),
                new RoomNumber("A001"),
                new SlotNumber(0));
    }

    @Test
    void shouldSuccessfullyCancelReservationWhenCalledByOwner() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        ReservedReservation reservedReservation = new ReservedReservation(reservationId, ownerId);

        OpenReservation result = reservedReservation.cancel(ownerId);

        assertNotNull(result);
        assertInstanceOf(OpenReservation.class, result);
    }

    @Test
    void shouldThrowExceptionWhenCancelledByDifferentResident() {
        // Given
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        ReservedReservation reservedReservation = new ReservedReservation(reservationId, ownerId);

        ResidentId differentResidentId = new ResidentId(UUID.randomUUID());

        assertThrows(
                IllegalArgumentException.class,
                () -> reservedReservation.cancel(differentResidentId)
        );
    }

    @Test
    void shouldThrowExceptionWhenCancelArgumentIsNull() {
        ReservedReservation reservedReservation = new ReservedReservation(
                createDummyReservationId(),
                new ResidentId(UUID.randomUUID())
        );

        assertThrows(NullPointerException.class, () -> reservedReservation.cancel(null));
    }

    @Test
    void shouldSuccessfullyAcceptReservationWhenCalledByStaff() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        ReservedReservation reservedReservation = new ReservedReservation(reservationId, ownerId);

        StaffId staffId = new StaffId(UUID.randomUUID());

        AcceptedReservation result = reservedReservation.accept(staffId);

        assertNotNull(result);
        assertInstanceOf(AcceptedReservation.class, result);
    }

    @Test
    void shouldThrowExceptionWhenAcceptArgumentIsNull() {
        ReservedReservation reservedReservation = new ReservedReservation(
                createDummyReservationId(),
                new ResidentId(UUID.randomUUID())
        );

        assertThrows(NullPointerException.class, () -> reservedReservation.accept(null));
    }

    @Test
    void shouldThrowExceptionWhenCreatedWithNullResidentId() {
        ReservationId reservationId = createDummyReservationId();

        assertThrows(NullPointerException.class, () -> new ReservedReservation(reservationId, null));
    }
}
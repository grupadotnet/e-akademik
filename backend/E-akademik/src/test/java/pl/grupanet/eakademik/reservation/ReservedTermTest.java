package pl.grupanet.eakademik.reservation;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReservedTermTest {
    private ReservationId createDummyReservationId() {
        return new ReservationId(
                new DormitoryId.Builder(
                        UniversityNames.POLITECHNIKA_KRAKOWSKA, "sth")
                        .build(),
                new RoomNumber("A001"),
                new SlotNumber(0));
    }

    @Test
    void shouldBuildReservation() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        ReservedTerm acceptedReservation = new ReservedTerm(reservationId, ownerId);

        assertEquals(acceptedReservation.getOwnerId(), ownerId);
        assertInstanceOf(Term.class, acceptedReservation);
    }

    @Test
    void shouldCorrectCreateSnapshot() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        ReservedTerm acceptedReservation = new ReservedTerm(reservationId, ownerId);

        TermSnapshot reservationSnapshot = acceptedReservation.toDto();
        TermSnapshot expecedReservationSnapshot = new TermSnapshot(
                reservationId, ReservationStatus.RESERVED, ownerId, null
        );
        assertEquals(reservationSnapshot, expecedReservationSnapshot);
    }

    @Test
    void shouldSuccessfullyCancelReservationWhenCalledByOwner() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        ReservedTerm reservedReservation = new ReservedTerm(reservationId, ownerId);

        OpenTerm result = reservedReservation.cancel(ownerId);

        assertNotNull(result);
        assertInstanceOf(OpenTerm.class, result);
    }

    @Test
    void shouldThrowExceptionWhenCancelledByDifferentResident() {
        // Given
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        ReservedTerm reservedReservation = new ReservedTerm(reservationId, ownerId);

        ResidentId differentResidentId = new ResidentId(UUID.randomUUID());

        assertThrows(
                IllegalArgumentException.class,
                () -> reservedReservation.cancel(differentResidentId)
        );
    }

    @Test
    void shouldThrowExceptionWhenCancelArgumentIsNull() {
        ReservedTerm reservedReservation = new ReservedTerm(
                createDummyReservationId(),
                new ResidentId(UUID.randomUUID())
        );

        assertThrows(NullPointerException.class, () -> reservedReservation.cancel(null));
    }

    @Test
    void shouldSuccessfullyAcceptReservationWhenCalledByStaff() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        ReservedTerm reservedReservation = new ReservedTerm(reservationId, ownerId);

        StaffId staffId = new StaffId(UUID.randomUUID());

        AcceptedTerm result = reservedReservation.accept(staffId);

        assertNotNull(result);
        assertInstanceOf(AcceptedTerm.class, result);
    }

    @Test
    void shouldThrowExceptionWhenAcceptArgumentIsNull() {
        ReservedTerm reservedReservation = new ReservedTerm(
                createDummyReservationId(),
                new ResidentId(UUID.randomUUID())
        );

        assertThrows(NullPointerException.class, () -> reservedReservation.accept(null));
    }

    @Test
    void shouldThrowExceptionWhenCreatedWithNullResidentId() {
        ReservationId reservationId = createDummyReservationId();

        assertThrows(NullPointerException.class, () -> new ReservedTerm(reservationId, null));
    }
}
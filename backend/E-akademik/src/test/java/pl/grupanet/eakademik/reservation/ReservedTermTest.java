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

import java.time.LocalDate;
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
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        ReservedTerm acceptedReservation = new ReservedTerm(reservationId, ownerId, localDate);

        assertEquals(acceptedReservation.getOwnerId(), ownerId);
        assertInstanceOf(Term.class, acceptedReservation);
    }

    @Test
    void shouldCorrectCreateSnapshot() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        ReservedTerm acceptedReservation = new ReservedTerm(reservationId, ownerId, localDate);

        TermSnapshot reservationSnapshot = acceptedReservation.toDto();
        TermSnapshot expecedReservationSnapshot = new TermSnapshot(
                reservationId, ReservationStatus.RESERVED, localDate, ownerId, null
        );
        assertEquals(reservationSnapshot, expecedReservationSnapshot);
    }

    @Test
    void shouldSuccessfullyCancelReservationWhenCalledByOwner() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        ReservedTerm reservedReservation = new ReservedTerm(reservationId, ownerId, localDate);

        OpenTerm result = reservedReservation.cancel(ownerId);

        assertNotNull(result);
        assertInstanceOf(OpenTerm.class, result);
    }

    @Test
    void shouldThrowExceptionWhenCancelledByDifferentResident() {
        // Given
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        ReservedTerm reservedReservation = new ReservedTerm(reservationId, ownerId, localDate);

        ResidentId differentResidentId = new ResidentId(UUID.randomUUID());

        assertThrows(
                IllegalArgumentException.class,
                () -> reservedReservation.cancel(differentResidentId)
        );
    }

    @Test
    void shouldThrowExceptionWhenCancelArgumentIsNull() {
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        ReservedTerm reservedReservation = new ReservedTerm(
                createDummyReservationId(),
                new ResidentId(UUID.randomUUID()),
                localDate
        );

        assertThrows(NullPointerException.class, () -> reservedReservation.cancel(null));
    }

    @Test
    void shouldSuccessfullyAcceptReservationWhenCalledByStaff() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        ReservedTerm reservedReservation = new ReservedTerm(reservationId, ownerId, localDate);

        StaffId staffId = new StaffId(UUID.randomUUID());

        AcceptedTerm result = reservedReservation.accept(staffId);

        assertNotNull(result);
        assertInstanceOf(AcceptedTerm.class, result);
    }

    @Test
    void shouldThrowExceptionWhenAcceptArgumentIsNull() {
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        ReservedTerm reservedReservation = new ReservedTerm(
                createDummyReservationId(),
                new ResidentId(UUID.randomUUID()),
                localDate
        );

        assertThrows(NullPointerException.class, () -> reservedReservation.accept(null));
    }

    @Test
    void shouldThrowExceptionWhenCreatedWithNullResidentId() {
        ReservationId reservationId = createDummyReservationId();
        LocalDate localDate = LocalDate.of(2020, 1, 1);
        ResidentId ownerId = new ResidentId(UUID.randomUUID());

        assertThrows(NullPointerException.class, () -> new ReservedTerm(reservationId, null, localDate));
        assertThrows(NullPointerException.class, () -> new ReservedTerm(reservationId, null, null));
        assertThrows(NullPointerException.class, () -> new ReservedTerm(reservationId, ownerId, null));
    }
}
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

class AcceptedTermTest {

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
        StaffId acceptedBy = new StaffId(UUID.randomUUID());
        AcceptedTerm acceptedReservation = new AcceptedTerm(reservationId, ownerId, acceptedBy);

        assertEquals(acceptedReservation.getAcceptedId(), acceptedBy);
        assertEquals(acceptedReservation.getOwnerId(), ownerId);
        assertInstanceOf(Term.class, acceptedReservation);
    }

    @Test
    void shouldCorrectCreateSnapshot() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedBy = new StaffId(UUID.randomUUID());
        AcceptedTerm acceptedReservation = new AcceptedTerm(reservationId, ownerId, acceptedBy);

        TermSnapshot reservationSnapshot = acceptedReservation.toDto();
        TermSnapshot expecedReservationSnapshot = new TermSnapshot(
                reservationId, ReservationStatus.ACCEPTED, ownerId, acceptedBy
        );
        assertEquals(reservationSnapshot, expecedReservationSnapshot);
    }

    @Test
    void shouldSuccessfullyCancelReservationWhenCalledByOwner() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedBy = new StaffId(UUID.randomUUID());
        AcceptedTerm acceptedReservation = new AcceptedTerm(reservationId, ownerId, acceptedBy);

        OpenTerm result = acceptedReservation.cancel(ownerId);

        assertNotNull(result);
        assertInstanceOf(OpenTerm.class, result);
    }

    @Test
    void shouldThrowExceptionWhenCancelledByDifferentResident() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedBy = new StaffId(UUID.randomUUID());
        AcceptedTerm acceptedReservation = new AcceptedTerm(reservationId, ownerId, acceptedBy);

        ResidentId differentResidentId = new ResidentId(UUID.randomUUID());

        assertThrows(
                IllegalArgumentException.class,
                () -> acceptedReservation.cancel(differentResidentId)
        );
    }

    @Test
    void shouldSuccessfullyCancelReservationWhenCalledByStaffMember() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedBy = new StaffId(UUID.randomUUID());
        StaffId anotherStaff = new StaffId(UUID.randomUUID());
        AcceptedTerm acceptedReservation = new AcceptedTerm(reservationId, ownerId, acceptedBy);

        OpenTerm resultByAcceptingStaff = acceptedReservation.cancel(acceptedBy);
        OpenTerm resultByAnotherStaff = acceptedReservation.cancel(anotherStaff);

        assertNotNull(resultByAcceptingStaff);
        assertInstanceOf(OpenTerm.class, resultByAcceptingStaff);

        assertNotNull(resultByAnotherStaff);
        assertInstanceOf(OpenTerm.class, resultByAnotherStaff);
    }

    @Test
    void shouldThrowExceptionWhenCancelByResidentArgumentIsNull() {
        AcceptedTerm acceptedReservation = new AcceptedTerm(
                createDummyReservationId(),
                new ResidentId(UUID.randomUUID()),
                new StaffId(UUID.randomUUID())
        );

        assertThrows(NullPointerException.class, () -> acceptedReservation.cancel((ResidentId) null));
    }

    @Test
    void shouldThrowExceptionWhenCancelByStaffArgumentIsNull() {
        AcceptedTerm acceptedReservation = new AcceptedTerm(
                createDummyReservationId(),
                new ResidentId(UUID.randomUUID()),
                new StaffId(UUID.randomUUID())
        );

        assertThrows(NullPointerException.class, () -> acceptedReservation.cancel((StaffId) null));
    }

    @Test
    void shouldThrowExceptionWhenCreatedWithNullResidentId() {
        ReservationId reservationId = createDummyReservationId();
        StaffId acceptedBy = new StaffId(UUID.randomUUID());

        assertThrows(NullPointerException.class, () -> new AcceptedTerm(reservationId, null, acceptedBy));
    }
}
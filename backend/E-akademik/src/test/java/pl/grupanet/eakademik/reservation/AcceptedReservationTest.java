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

class AcceptedReservationTest {

    private ReservationId createDummyReservationId() {
        return new ReservationId(
                new DormitoryId.Builder(
                        UniversityNames.POLITECHNIKA_KRAKOWSKA,"sth")
                        .build(),
                new RoomNumber("A001"),
                new SlotNumber(0));
    }

    @Test
    void shouldSuccessfullyCancelReservationWhenCalledByOwner() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedBy = new StaffId(UUID.randomUUID());
        AcceptedReservation acceptedReservation = new AcceptedReservation(reservationId, ownerId, acceptedBy);

        OpenReservation result = acceptedReservation.cancel(ownerId);

        assertNotNull(result);
        assertInstanceOf(OpenReservation.class, result);
    }

    @Test
    void shouldThrowExceptionWhenCancelledByDifferentResident() {
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedBy = new StaffId(UUID.randomUUID());
        AcceptedReservation acceptedReservation = new AcceptedReservation(reservationId, ownerId, acceptedBy);

        ResidentId differentResidentId = new ResidentId(UUID.randomUUID());

        assertThrows(
                IllegalArgumentException.class,
                () -> acceptedReservation.cancel(differentResidentId)
        );
    }

    @Test
    void shouldSuccessfullyCancelReservationWhenCalledByStaffMember() {
        // Given
        ReservationId reservationId = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedBy = new StaffId(UUID.randomUUID());
        StaffId anotherStaff = new StaffId(UUID.randomUUID());
        AcceptedReservation acceptedReservation = new AcceptedReservation(reservationId, ownerId, acceptedBy);

        OpenReservation resultByAcceptingStaff = acceptedReservation.cancel(acceptedBy);
        OpenReservation resultByAnotherStaff = acceptedReservation.cancel(anotherStaff);

        assertNotNull(resultByAcceptingStaff);
        assertInstanceOf(OpenReservation.class, resultByAcceptingStaff);

        assertNotNull(resultByAnotherStaff);
        assertInstanceOf(OpenReservation.class, resultByAnotherStaff);
    }

    @Test
    void shouldThrowExceptionWhenCancelByResidentArgumentIsNull() {
        AcceptedReservation acceptedReservation = new AcceptedReservation(
                createDummyReservationId(),
                new ResidentId(UUID.randomUUID()),
                new StaffId(UUID.randomUUID())
        );

        assertThrows(NullPointerException.class, () -> acceptedReservation.cancel((ResidentId) null));
    }

    @Test
    void shouldThrowExceptionWhenCancelByStaffArgumentIsNull() {
        AcceptedReservation acceptedReservation = new AcceptedReservation(
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

        assertThrows(NullPointerException.class, () -> new AcceptedReservation(reservationId, null, acceptedBy));
    }
}
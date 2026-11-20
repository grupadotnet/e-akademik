package pl.grupanet.eakademik.reservation.dto;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.ReservationId;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TermSnapshotTest {

    private ReservationId createDummyReservationId() {
        DormitoryId dormitoryId = new DormitoryId.Builder(UniversityNames.values()[0], "Akademik 1").build();
        return new ReservationId(dormitoryId, new RoomNumber("101"), new SlotNumber(1));
    }

    @Test
    void shouldCreateReservationSnapshotWithAllFields() {
        ReservationId id = createDummyReservationId();
        ReservationStatus status = ReservationStatus.ACCEPTED;
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedById = new StaffId(UUID.randomUUID());

        TermSnapshot snapshot = new TermSnapshot(id, status, getDummyDate(), ownerId, acceptedById);

        assertNotNull(snapshot);
        assertEquals(id, snapshot.id());
        assertEquals(status, snapshot.status());
        assertEquals(ownerId, snapshot.ownerId());
        assertEquals(acceptedById, snapshot.acceptedById());
    }

    @Test
    void shouldAllowNullForOwnerIdAndAcceptedById() {
        ReservationId id = createDummyReservationId();
        ReservationStatus status = ReservationStatus.OPEN;

        TermSnapshot snapshot = new TermSnapshot(id, status, getDummyDate(), null, null);

        assertNotNull(snapshot);
        assertEquals(id, snapshot.id());
        assertEquals(status, snapshot.status());
        assertNull(snapshot.ownerId());
        assertNull(snapshot.acceptedById());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedById = new StaffId(UUID.randomUUID());

        assertThrows(
                NullPointerException.class,
                () -> new TermSnapshot(null, ReservationStatus.RESERVED, getDummyDate(), ownerId, acceptedById)
        );
    }

    @Test
    void shouldThrowExceptionWhenStatusIsNull() {
        ReservationId id = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedById = new StaffId(UUID.randomUUID());

        assertThrows(
                NullPointerException.class,
                () -> new TermSnapshot(id, null, getDummyDate(), ownerId, acceptedById)
        );
    }

    @Test
    void shouldBeEqualToItself() {
        ReservationId id = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());
        StaffId acceptedById = new StaffId(UUID.randomUUID());
        TermSnapshot snapshot = new TermSnapshot(id, ReservationStatus.ACCEPTED, getDummyDate(), ownerId, acceptedById);

        assertEquals(snapshot, snapshot);
        assertEquals(snapshot.hashCode(), snapshot.hashCode());
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreIdentical() {
        UUID ownerUuid = UUID.randomUUID();
        UUID staffUuid = UUID.randomUUID();

        TermSnapshot first = new TermSnapshot(
                createDummyReservationId(),
                ReservationStatus.ACCEPTED,
                getDummyDate(),
                new ResidentId(ownerUuid),
                new StaffId(staffUuid)
        );
        TermSnapshot second = new TermSnapshot(
                createDummyReservationId(),
                ReservationStatus.ACCEPTED,
                getDummyDate(),
                new ResidentId(ownerUuid),
                new StaffId(staffUuid)
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldBeEqualWhenNullableFieldsAreNull() {
        TermSnapshot first = new TermSnapshot(createDummyReservationId(), ReservationStatus.OPEN, getDummyDate(),
                null, null);
        TermSnapshot second = new TermSnapshot(createDummyReservationId(), ReservationStatus.OPEN, getDummyDate(),
                null, null);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        TermSnapshot snapshot = new TermSnapshot(createDummyReservationId(), ReservationStatus.OPEN, getDummyDate(),
                null, null);

        assertNotEquals(null, snapshot);
        assertNotEquals("not a snapshot", snapshot);
    }

    @Test
    void shouldNotBeEqualWhenIdDiffers() {
        DormitoryId dormitoryId = new DormitoryId.Builder(UniversityNames.values()[0], "Akademik 1").build();
        ReservationId differentId = new ReservationId(dormitoryId, new RoomNumber("102"), new SlotNumber(1));
        ResidentId ownerId = new ResidentId(UUID.randomUUID());

        TermSnapshot first = new TermSnapshot(createDummyReservationId(), ReservationStatus.RESERVED, getDummyDate(),
                ownerId, null);
        TermSnapshot second = new TermSnapshot(differentId, ReservationStatus.RESERVED, getDummyDate(),
                ownerId, null);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenStatusDiffers() {
        ReservationId id = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());

        TermSnapshot first = new TermSnapshot(id, ReservationStatus.OPEN, getDummyDate(), ownerId, null);
        TermSnapshot second = new TermSnapshot(id, ReservationStatus.RESERVED, getDummyDate(), ownerId, null);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenOwnerIdDiffers() {
        ReservationId id = createDummyReservationId();

        TermSnapshot withOwner = new TermSnapshot(id, ReservationStatus.RESERVED, getDummyDate(),
                new ResidentId(UUID.randomUUID()), null);
        TermSnapshot withDifferentOwner = new TermSnapshot(id, ReservationStatus.RESERVED, getDummyDate(),
                new ResidentId(UUID.randomUUID()), null);
        TermSnapshot withoutOwner = new TermSnapshot(id, ReservationStatus.RESERVED, getDummyDate(),
                null, null);

        assertNotEquals(withOwner, withDifferentOwner);
        assertNotEquals(withOwner, withoutOwner);
    }

    @Test
    void shouldNotBeEqualWhenAcceptedByIdDiffers() {
        ReservationId id = createDummyReservationId();
        ResidentId ownerId = new ResidentId(UUID.randomUUID());

        TermSnapshot withStaff = new TermSnapshot(id, ReservationStatus.ACCEPTED, getDummyDate(), ownerId,
                new StaffId(UUID.randomUUID()));
        TermSnapshot withDifferentStaff = new TermSnapshot(id, ReservationStatus.ACCEPTED, getDummyDate(), ownerId,
                new StaffId(UUID.randomUUID()));
        TermSnapshot withoutStaff = new TermSnapshot(id, ReservationStatus.ACCEPTED, getDummyDate(), ownerId,
                null);

        assertNotEquals(withStaff, withDifferentStaff);
        assertNotEquals(withStaff, withoutStaff);
    }

    private static LocalDate getDummyDate() {
        return LocalDate.now();
    }
}
package pl.grupanet.eakademik.reservation;

import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import static org.junit.jupiter.api.Assertions.*;

class ReservationIdTest {

    private DormitoryId createDummyDormitoryId() {
        return new DormitoryId.Builder(UniversityNames.values()[0], "Akademik 1").build();
    }

    @Test
    void shouldCreateReservationIdWithValidFields() {
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("101");
        SlotNumber slotNumber = new SlotNumber(1);

        ReservationId reservationId = new ReservationId(dormitoryId, roomNumber, slotNumber);

        assertNotNull(reservationId);
        assertEquals(dormitoryId, reservationId.dormitoryId());
        assertEquals(roomNumber, reservationId.roomNumber());
        assertEquals(slotNumber, reservationId.slotNumber());
    }

    @Test
    void shouldThrowExceptionWhenAnyFieldIsNull() {
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("101");
        SlotNumber slotNumber = new SlotNumber(1);

        assertThrows(NullPointerException.class, () -> new ReservationId(null, roomNumber, slotNumber));
        assertThrows(NullPointerException.class, () -> new ReservationId(dormitoryId, null, slotNumber));
        assertThrows(NullPointerException.class, () -> new ReservationId(dormitoryId, roomNumber, null));
    }

    @Test
    void shouldBeEqualToItself() {
        ReservationId reservationId = new ReservationId(
                createDummyDormitoryId(),
                new RoomNumber("101"),
                new SlotNumber(1)
        );

        assertEquals(reservationId, reservationId);
        assertEquals(reservationId.hashCode(), reservationId.hashCode());
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreIdentical() {
        ReservationId first = new ReservationId(
                new DormitoryId.Builder(UniversityNames.values()[0], "Akademik 1").build(),
                new RoomNumber("101"),
                new SlotNumber(1)
        );
        ReservationId second = new ReservationId(
                new DormitoryId.Builder(UniversityNames.values()[0], "Akademik 1").build(),
                new RoomNumber("101"),
                new SlotNumber(1)
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        ReservationId reservationId = new ReservationId(
                createDummyDormitoryId(),
                new RoomNumber("101"),
                new SlotNumber(1)
        );

        assertNotEquals(null, reservationId);
        assertNotEquals("101-1", reservationId);
    }

    @Test
    void shouldNotBeEqualWhenDormitoryNameDiffers() {
        ReservationId first = new ReservationId(
                new DormitoryId.Builder(UniversityNames.values()[0], "Akademik 1").build(),
                new RoomNumber("101"),
                new SlotNumber(1)
        );
        ReservationId second = new ReservationId(
                new DormitoryId.Builder(UniversityNames.values()[0], "Akademik 2").build(),
                new RoomNumber("101"),
                new SlotNumber(1)
        );

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenUniversityDiffers() {
        UniversityNames[] universities = UniversityNames.values();
        if (universities.length < 2) {
            return;
        }

        ReservationId first = new ReservationId(
                new DormitoryId.Builder(universities[0], "Akademik 1").build(),
                new RoomNumber("101"),
                new SlotNumber(1)
        );
        ReservationId second = new ReservationId(
                new DormitoryId.Builder(universities[1], "Akademik 1").build(),
                new RoomNumber("101"),
                new SlotNumber(1)
        );

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenRoomNumberDiffers() {
        DormitoryId dormitoryId = createDummyDormitoryId();
        ReservationId first = new ReservationId(dormitoryId, new RoomNumber("101"), new SlotNumber(1));
        ReservationId second = new ReservationId(dormitoryId, new RoomNumber("102"), new SlotNumber(1));

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenSlotNumberDiffers() {
        DormitoryId dormitoryId = createDummyDormitoryId();
        ReservationId first = new ReservationId(dormitoryId, new RoomNumber("101"), new SlotNumber(1));
        ReservationId second = new ReservationId(dormitoryId, new RoomNumber("101"), new SlotNumber(2));

        assertNotEquals(first, second);
    }
}
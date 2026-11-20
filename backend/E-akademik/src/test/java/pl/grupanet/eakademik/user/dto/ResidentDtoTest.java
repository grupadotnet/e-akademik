package pl.grupanet.eakademik.user.dto;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResidentDtoTest {

    private DormitoryId createDummyDormitoryId() {
        return new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build();
    }

    @Test
    void shouldCreateResidentDtoWithAllFields() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name fullName = new Name("Jan Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto dto = new ResidentDto(id, fullName, dormitoryId, roomNumber);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals(fullName, dto.fullName());
        assertEquals(dormitoryId, dto.dormitoryId());
        assertEquals(roomNumber, dto.roomNumber());
    }

    @Test
    void shouldThrowExceptionWhenAnyFieldIsNull() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name fullName = new Name("Jan Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        assertThrows(NullPointerException.class, () -> new ResidentDto(null, fullName, dormitoryId, roomNumber));
        assertThrows(NullPointerException.class, () -> new ResidentDto(id, null, dormitoryId, roomNumber));
        assertThrows(NullPointerException.class, () -> new ResidentDto(id, fullName, null, roomNumber));
        assertThrows(NullPointerException.class, () -> new ResidentDto(id, fullName, dormitoryId, null));
    }

    @Test
    void shouldBeEqualToItself() {
        ResidentDto dto = new ResidentDto(
                new ResidentId(UUID.randomUUID()),
                new Name("Jan Kowalski"),
                createDummyDormitoryId(),
                new RoomNumber("204B")
        );

        assertEquals(dto, dto);
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreIdentical() {
        UUID uuid = UUID.randomUUID();

        ResidentDto first = new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan Kowalski"),
                createDummyDormitoryId(),
                new RoomNumber("204B")
        );
        ResidentDto second = new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan Kowalski"),
                createDummyDormitoryId(),
                new RoomNumber("204B")
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        ResidentDto dto = new ResidentDto(
                new ResidentId(UUID.randomUUID()),
                new Name("Jan Kowalski"),
                createDummyDormitoryId(),
                new RoomNumber("204B")
        );

        assertNotEquals(null, dto);
        assertNotEquals("not a dto", dto);
    }

    @Test
    void shouldNotBeEqualWhenIdDiffers() {
        Name fullName = new Name("Jan Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto first = new ResidentDto(new ResidentId(UUID.randomUUID()), fullName, dormitoryId, roomNumber);
        ResidentDto second = new ResidentDto(new ResidentId(UUID.randomUUID()), fullName, dormitoryId, roomNumber);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenFullNameDiffers() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto first = new ResidentDto(id, new Name("Jan Kowalski"), dormitoryId, roomNumber);
        ResidentDto second = new ResidentDto(id, new Name("Anna Nowak"), dormitoryId, roomNumber);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenDormitoryIdDiffers() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name fullName = new Name("Jan Kowalski");
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto first = new ResidentDto(
                id,
                fullName,
                new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build(),
                roomNumber
        );
        ResidentDto second = new ResidentDto(
                id,
                fullName,
                new DormitoryId.Builder(UniversityNames.values()[0], "Ds-1").build(),
                roomNumber
        );

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenRoomNumberDiffers() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name fullName = new Name("Jan Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();

        ResidentDto first = new ResidentDto(id, fullName, dormitoryId, new RoomNumber("204B"));
        ResidentDto second = new ResidentDto(id, fullName, dormitoryId, new RoomNumber("205A"));

        assertNotEquals(first, second);
    }
}

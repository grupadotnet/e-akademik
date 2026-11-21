package pl.grupanet.eakademik.user.dto;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResidentDtoTest {

    private DormitoryId createDummyDormitoryId() {
        return new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build();
    }

    @Test
    void shouldCreateResidentDtoWithAllFields() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name name = new Name("Jan");
        Name surname = new Name("Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto dto = new ResidentDto(id, name, surname, dormitoryId, roomNumber);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals(name, dto.name());
        assertEquals(surname, dto.surname());
        assertEquals(dormitoryId, dto.dormitoryId());
        assertEquals(roomNumber, dto.roomNumber());
    }

    @Test
    void shouldThrowExceptionWhenAnyFieldIsNull() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name name = new Name("Jan");
        Name surname = new Name("Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        assertThrows(NullPointerException.class, () -> new ResidentDto(null, name, surname, dormitoryId, roomNumber));
        assertThrows(NullPointerException.class, () -> new ResidentDto(id, null, surname, dormitoryId, roomNumber));
        assertThrows(NullPointerException.class, () -> new ResidentDto(id, name, null, dormitoryId, roomNumber));
        assertThrows(NullPointerException.class, () -> new ResidentDto(id, name, surname, null, roomNumber));
        assertThrows(NullPointerException.class, () -> new ResidentDto(id, name, surname, dormitoryId, null));
    }

    @Test
    void shouldBeEqualToItself() {
        ResidentDto dto = new ResidentDto(
                new ResidentId(UUID.randomUUID()),
                new Name("Jan"),
                new Name("Kowalski"),
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
                new Name("Jan"),
                new Name("Kowalski"),
                createDummyDormitoryId(),
                new RoomNumber("204B")
        );
        ResidentDto second = new ResidentDto(
                new ResidentId(uuid),
                new Name("Jan"),
                new Name("Kowalski"),
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
                new Name("Jan"),
                new Name("Kowalski"),
                createDummyDormitoryId(),
                new RoomNumber("204B")
        );

        assertNotEquals(null, dto);
        assertNotEquals("not a dto", dto);
    }

    @Test
    void shouldNotBeEqualWhenIdDiffers() {
        Name name = new Name("Jan");
        Name surname = new Name("Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto first = new ResidentDto(new ResidentId(UUID.randomUUID()), name, surname, dormitoryId, roomNumber);
        ResidentDto second = new ResidentDto(new ResidentId(UUID.randomUUID()), name, surname, dormitoryId, roomNumber);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenNameDiffers() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name surname = new Name("Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto first = new ResidentDto(id, new Name("Jan"), surname, dormitoryId, roomNumber);
        ResidentDto second = new ResidentDto(id, new Name("Adam"), surname, dormitoryId, roomNumber);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenSurnameDiffers() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name name = new Name("Jan");
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto first = new ResidentDto(id, name, new Name("Kowalski"), dormitoryId, roomNumber);
        ResidentDto second = new ResidentDto(id, name, new Name("Nowak"), dormitoryId, roomNumber);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenDormitoryIdDiffers() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name name = new Name("Jan");
        Name surname = new Name("Kowalski");
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto first = new ResidentDto(
                id,
                name,
                surname,
                new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build(),
                roomNumber
        );
        ResidentDto second = new ResidentDto(
                id,
                name,
                surname,
                new DormitoryId.Builder(UniversityNames.values()[0], "Ds-1").build(),
                roomNumber
        );

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenRoomNumberDiffers() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name name = new Name("Jan");
        Name surname = new Name("Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();

        ResidentDto first = new ResidentDto(id, name, surname, dormitoryId, new RoomNumber("204B"));
        ResidentDto second = new ResidentDto(id, name, surname, dormitoryId, new RoomNumber("205A"));

        assertNotEquals(first, second);
    }

    @Test
    void shouldCreateIdDto() {
        ResidentId id = new ResidentId(UUID.randomUUID());
        Name name = new Name("Jan");
        Name surname = new Name("Kowalski");
        DormitoryId dormitoryId = createDummyDormitoryId();
        RoomNumber roomNumber = new RoomNumber("204B");

        ResidentDto residentDto = new ResidentDto(id, name, surname, dormitoryId, roomNumber);
        ResidentIdDto expectedResidentIdDto = new ResidentIdDto(
                id.getIdAsUUID(),
                name.toString(),
                surname.toString(),
                dormitoryId.toString());

        assertEquals(expectedResidentIdDto, residentDto.toCommonDto());
    }
}

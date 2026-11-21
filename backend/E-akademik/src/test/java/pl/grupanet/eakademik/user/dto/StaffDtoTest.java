package pl.grupanet.eakademik.user.dto;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.users.StaffId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StaffDtoTest {

    private DormitoryId createDummyDormitoryId() {
        return new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build();
    }

    @Test
    void shouldCreateStaffDtoWithAllFields() {
        StaffId id = new StaffId(UUID.randomUUID());
        Name name = new Name("Anna");
        Name surname = new Name("Nowak");
        DormitoryId dormitoryId = createDummyDormitoryId();

        StaffDto dto = new StaffDto(id, name, surname, dormitoryId);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals(name, dto.name());
        assertEquals(surname, dto.surname());
        assertEquals(dormitoryId, dto.dormitoryId());
    }

    @Test
    void shouldThrowExceptionWhenAnyFieldIsNull() {
        StaffId id = new StaffId(UUID.randomUUID());
        Name name = new Name("Anna");
        Name surname = new Name("Nowak");
        DormitoryId dormitoryId = createDummyDormitoryId();

        assertThrows(NullPointerException.class, () -> new StaffDto(null, name, surname, dormitoryId));
        assertThrows(NullPointerException.class, () -> new StaffDto(id, null, surname, dormitoryId));
        assertThrows(NullPointerException.class, () -> new StaffDto(id, name, null, dormitoryId));
        assertThrows(NullPointerException.class, () -> new StaffDto(id, name, surname, null));
    }

    @Test
    void shouldBeEqualToItself() {
        StaffDto dto = new StaffDto(
                new StaffId(UUID.randomUUID()),
                new Name("Anna"),
                new Name("Nowak"),
                createDummyDormitoryId()
        );

        assertEquals(dto, dto);
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreIdentical() {
        UUID uuid = UUID.randomUUID();

        StaffDto first = new StaffDto(
                new StaffId(uuid),
                new Name("Anna"),
                new Name("Nowak"),
                createDummyDormitoryId()
        );
        StaffDto second = new StaffDto(
                new StaffId(uuid),
                new Name("Anna"),
                new Name("Nowak"),
                createDummyDormitoryId()
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        StaffDto dto = new StaffDto(
                new StaffId(UUID.randomUUID()),
                new Name("Anna"),
                new Name("Nowak"),
                createDummyDormitoryId()
        );

        assertNotEquals(null, dto);
        assertNotEquals("not a dto", dto);
    }

    @Test
    void shouldNotBeEqualWhenIdDiffers() {
        Name name = new Name("Anna");
        Name surname = new Name("Nowak");
        DormitoryId dormitoryId = createDummyDormitoryId();

        StaffDto first = new StaffDto(new StaffId(UUID.randomUUID()), name, surname, dormitoryId);
        StaffDto second = new StaffDto(new StaffId(UUID.randomUUID()), name, surname, dormitoryId);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenNameDiffers() {
        StaffId id = new StaffId(UUID.randomUUID());
        Name surname = new Name("Nowak");
        DormitoryId dormitoryId = createDummyDormitoryId();

        StaffDto first = new StaffDto(id, new Name("Anna"), surname, dormitoryId);
        StaffDto second = new StaffDto(id, new Name("Maria"), surname, dormitoryId);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenSurnameDiffers() {
        StaffId id = new StaffId(UUID.randomUUID());
        Name name = new Name("Anna");
        DormitoryId dormitoryId = createDummyDormitoryId();

        StaffDto first = new StaffDto(id, name, new Name("Nowak"), dormitoryId);
        StaffDto second = new StaffDto(id, name, new Name("Kowalska"), dormitoryId);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenDormitoryIdDiffers() {
        StaffId id = new StaffId(UUID.randomUUID());
        Name name = new Name("Anna");
        Name surname = new Name("Nowak");

        StaffDto first = new StaffDto(
                id,
                name,
                surname,
                new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build()
        );
        StaffDto second = new StaffDto(
                id,
                name,
                surname,
                new DormitoryId.Builder(UniversityNames.values()[0], "Ds-1").build()
        );

        assertNotEquals(first, second);
    }
}

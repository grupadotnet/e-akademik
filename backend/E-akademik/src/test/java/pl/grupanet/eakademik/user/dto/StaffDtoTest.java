package pl.grupanet.eakademik.user.dto;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.users.StaffId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StaffDtoTest {

    private DormitoryId createDummyDormitoryId() {
        return new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build();
    }

    @Test
    void shouldCreateStaffDtoWithAllFields() {
        StaffId id = new StaffId(UUID.randomUUID());
        Name fullName = new Name("Anna Nowak");
        DormitoryId dormitoryId = createDummyDormitoryId();

        StaffDto dto = new StaffDto(id, fullName, dormitoryId);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals(fullName, dto.fullName());
        assertEquals(dormitoryId, dto.dormitoryId());
    }

    @Test
    void shouldThrowExceptionWhenAnyFieldIsNull() {
        StaffId id = new StaffId(UUID.randomUUID());
        Name fullName = new Name("Anna Nowak");
        DormitoryId dormitoryId = createDummyDormitoryId();

        assertThrows(NullPointerException.class, () -> new StaffDto(null, fullName, dormitoryId));
        assertThrows(NullPointerException.class, () -> new StaffDto(id, null, dormitoryId));
        assertThrows(NullPointerException.class, () -> new StaffDto(id, fullName, null));
    }

    @Test
    void shouldBeEqualToItself() {
        StaffDto dto = new StaffDto(
                new StaffId(UUID.randomUUID()),
                new Name("Anna Nowak"),
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
                new Name("Anna Nowak"),
                createDummyDormitoryId()
        );
        StaffDto second = new StaffDto(
                new StaffId(uuid),
                new Name("Anna Nowak"),
                createDummyDormitoryId()
        );

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        StaffDto dto = new StaffDto(
                new StaffId(UUID.randomUUID()),
                new Name("Anna Nowak"),
                createDummyDormitoryId()
        );

        assertNotEquals(null, dto);
        assertNotEquals("not a dto", dto);
    }

    @Test
    void shouldNotBeEqualWhenIdDiffers() {
        Name fullName = new Name("Anna Nowak");
        DormitoryId dormitoryId = createDummyDormitoryId();

        StaffDto first = new StaffDto(new StaffId(UUID.randomUUID()), fullName, dormitoryId);
        StaffDto second = new StaffDto(new StaffId(UUID.randomUUID()), fullName, dormitoryId);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenFullNameDiffers() {
        StaffId id = new StaffId(UUID.randomUUID());
        DormitoryId dormitoryId = createDummyDormitoryId();

        StaffDto first = new StaffDto(id, new Name("Anna Nowak"), dormitoryId);
        StaffDto second = new StaffDto(id, new Name("Jan Kowalski"), dormitoryId);

        assertNotEquals(first, second);
    }

    @Test
    void shouldNotBeEqualWhenDormitoryIdDiffers() {
        StaffId id = new StaffId(UUID.randomUUID());
        Name fullName = new Name("Anna Nowak");

        StaffDto first = new StaffDto(
                id,
                fullName,
                new DormitoryId.Builder(UniversityNames.values()[0], "Ds-3 Bartek").build()
        );
        StaffDto second = new StaffDto(
                id,
                fullName,
                new DormitoryId.Builder(UniversityNames.values()[0], "Ds-1").build()
        );

        assertNotEquals(first, second);
    }
}

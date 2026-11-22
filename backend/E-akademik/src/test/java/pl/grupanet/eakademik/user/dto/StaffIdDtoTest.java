package pl.grupanet.eakademik.user.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StaffIdDtoTest {

    @Test
    void shouldCreateStaffIdDtoAndReturnCorrectFieldValues() {
        UUID userId = UUID.randomUUID();
        String name = "Anna";
        String surname = "Nowak";

        StaffIdDto dto = new StaffIdDto(userId, name, surname);

        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.name()).isEqualTo(name);
        assertThat(dto.surname()).isEqualTo(surname);
    }

    @Test
    void shouldBeEqualToItself() {
        StaffIdDto dto = new StaffIdDto(UUID.randomUUID(), "Anna", "Nowak");

        assertThat(dto).isEqualTo(dto);
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreTheSame() {
        UUID userId = UUID.randomUUID();
        StaffIdDto dto1 = new StaffIdDto(userId, "Anna", "Nowak");
        StaffIdDto dto2 = new StaffIdDto(userId, "Anna", "Nowak");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto2).isEqualTo(dto1);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldBeEqualWhenFieldsAreNull() {
        StaffIdDto dto1 = new StaffIdDto(null, null, null);
        StaffIdDto dto2 = new StaffIdDto(null, null, null);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenUserIdDiffers() {
        StaffIdDto dto1 = new StaffIdDto(UUID.randomUUID(), "Anna", "Nowak");
        StaffIdDto dto2 = new StaffIdDto(UUID.randomUUID(), "Anna", "Nowak");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenNameDiffers() {
        UUID userId = UUID.randomUUID();
        StaffIdDto dto1 = new StaffIdDto(userId, "Anna", "Nowak");
        StaffIdDto dto2 = new StaffIdDto(userId, "Maria", "Nowak");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenSurnameDiffers() {
        UUID userId = UUID.randomUUID();
        StaffIdDto dto1 = new StaffIdDto(userId, "Anna", "Nowak");
        StaffIdDto dto2 = new StaffIdDto(userId, "Anna", "Kowalska");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        StaffIdDto dto = new StaffIdDto(UUID.randomUUID(), "Anna", "Nowak");

        assertThat(dto).isNotEqualTo(null);
        assertThat(dto).isNotEqualTo("jakiś string");
    }

    @Test
    void shouldContainAllFieldsInToString() {
        UUID userId = UUID.randomUUID();
        StaffIdDto dto = new StaffIdDto(userId, "Anna", "Nowak");

        String result = dto.toString();

        assertThat(result)
                .contains("StaffIdDto")
                .contains(userId.toString())
                .contains("Anna")
                .contains("Nowak");
    }
}
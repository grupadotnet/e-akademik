package pl.grupanet.eakademik.user.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ResidentIdDtoTest {

    @Test
    void shouldCreateResidentIdDtoAndReturnCorrectFieldValues() {
        UUID userId = UUID.randomUUID();
        String name = "Jan";
        String surname = "Kowalski";
        String roomNumber = "101A";

        ResidentIdDto dto = new ResidentIdDto(userId, name, surname, roomNumber);

        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.name()).isEqualTo(name);
        assertThat(dto.surname()).isEqualTo(surname);
        assertThat(dto.roomNumber()).isEqualTo(roomNumber);
    }

    @Test
    void shouldBeEqualToItself() {
        ResidentIdDto dto = new ResidentIdDto(UUID.randomUUID(), "Jan", "Kowalski", "101A");

        assertThat(dto).isEqualTo(dto);
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreTheSame() {
        UUID userId = UUID.randomUUID();
        ResidentIdDto dto1 = new ResidentIdDto(userId, "Jan", "Kowalski", "101A");
        ResidentIdDto dto2 = new ResidentIdDto(userId, "Jan", "Kowalski", "101A");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto2).isEqualTo(dto1);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldBeEqualWhenFieldsAreNull() {
        ResidentIdDto dto1 = new ResidentIdDto(null, null, null, null);
        ResidentIdDto dto2 = new ResidentIdDto(null, null, null, null);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenUserIdDiffers() {
        ResidentIdDto dto1 = new ResidentIdDto(UUID.randomUUID(), "Jan", "Kowalski", "101A");
        ResidentIdDto dto2 = new ResidentIdDto(UUID.randomUUID(), "Jan", "Kowalski", "101A");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenNameDiffers() {
        UUID userId = UUID.randomUUID();
        ResidentIdDto dto1 = new ResidentIdDto(userId, "Jan", "Kowalski", "101A");
        ResidentIdDto dto2 = new ResidentIdDto(userId, "Adam", "Kowalski", "101A");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenSurnameDiffers() {
        UUID userId = UUID.randomUUID();
        ResidentIdDto dto1 = new ResidentIdDto(userId, "Jan", "Kowalski", "101A");
        ResidentIdDto dto2 = new ResidentIdDto(userId, "Jan", "Nowak", "101A");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenRoomNumberDiffers() {
        UUID userId = UUID.randomUUID();
        ResidentIdDto dto1 = new ResidentIdDto(userId, "Jan", "Kowalski", "101A");
        ResidentIdDto dto2 = new ResidentIdDto(userId, "Jan", "Kowalski", "202B");

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        ResidentIdDto dto = new ResidentIdDto(UUID.randomUUID(), "Jan", "Kowalski", "101A");

        assertThat(dto).isNotEqualTo(null);
        assertThat(dto).isNotEqualTo("jakiś string");
    }

    @Test
    void shouldContainAllFieldsInToString() {
        UUID userId = UUID.randomUUID();
        ResidentIdDto dto = new ResidentIdDto(userId, "Jan", "Kowalski", "101A");

        String result = dto.toString();

        assertThat(result)
                .contains("ResidentIdDto")
                .contains(userId.toString())
                .contains("Jan")
                .contains("Kowalski")
                .contains("101A");
    }
}
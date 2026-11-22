package pl.grupanet.eakademik.reservation.services.dto;

import org.commons.time.TimeSlot;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.user.dto.ResidentIdDto;
import pl.grupanet.eakademik.user.dto.StaffIdDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationByDateDTOTest {

    private ReservationByRoomsDTO createReservationByRoomsDto(String roomNumber, int slotNumber) {
        ReservationDTO reservation = new ReservationDTO(
                roomNumber,
                new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0)),
                slotNumber,
                ReservationStatus.values()[0],
                new ResidentIdDto(UUID.randomUUID(), "Jan", "Kowalski", "101A"),
                new StaffIdDto(UUID.randomUUID(), "Anna", "Nowak")
        );
        HashMap<String, List<ReservationDTO>> roomsMap = new HashMap<>();
        roomsMap.put(roomNumber, List.of(reservation));
        return new ReservationByRoomsDTO(roomsMap);
    }

    @Test
    void shouldCreateReservationByDateDtoAndReturnCorrectDatesMap() {
        LocalDate date = LocalDate.of(2026, 11, 21);
        ReservationByRoomsDTO byRooms = createReservationByRoomsDto("101A", 1);
        HashMap<LocalDate, ReservationByRoomsDTO> datesMap = new HashMap<>();
        datesMap.put(date, byRooms);

        ReservationByDateDTO dto = new ReservationByDateDTO(datesMap);

        assertThat(dto.dates()).isEqualTo(datesMap);
        assertThat(dto.dates()).containsEntry(date, byRooms);
    }

    @Test
    void shouldBeEqualToItself() {
        ReservationByDateDTO dto = new ReservationByDateDTO(new HashMap<>());

        assertThat(dto).isEqualTo(dto);
    }

    @Test
    void shouldBeEqualWhenDatesMapsContainSameEntries() {
        LocalDate date = LocalDate.of(2026, 11, 21);
        ReservationByRoomsDTO byRooms = createReservationByRoomsDto("101A", 1);

        HashMap<LocalDate, ReservationByRoomsDTO> map1 = new HashMap<>();
        map1.put(date, byRooms);

        HashMap<LocalDate, ReservationByRoomsDTO> map2 = new HashMap<>();
        map2.put(date, byRooms);

        ReservationByDateDTO dto1 = new ReservationByDateDTO(map1);
        ReservationByDateDTO dto2 = new ReservationByDateDTO(map2);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto2).isEqualTo(dto1);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldBeEqualWhenDatesIsNull() {
        ReservationByDateDTO dto1 = new ReservationByDateDTO(null);
        ReservationByDateDTO dto2 = new ReservationByDateDTO(null);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDatesMapDiffers() {
        HashMap<LocalDate, ReservationByRoomsDTO> map1 = new HashMap<>();
        map1.put(LocalDate.of(2026, 11, 21), createReservationByRoomsDto("101A", 1));

        HashMap<LocalDate, ReservationByRoomsDTO> map2 = new HashMap<>();
        map2.put(LocalDate.of(2026, 11, 22), createReservationByRoomsDto("101A", 1));

        ReservationByDateDTO dto1 = new ReservationByDateDTO(map1);
        ReservationByDateDTO dto2 = new ReservationByDateDTO(map2);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        ReservationByDateDTO dto = new ReservationByDateDTO(new HashMap<>());

        assertThat(dto).isNotEqualTo(null);
        assertThat(dto).isNotEqualTo("sth");
    }

    @Test
    void shouldContainDatesInToString() {
        LocalDate date = LocalDate.of(2026, 11, 21);
        HashMap<LocalDate, ReservationByRoomsDTO> map = new HashMap<>();
        map.put(date, createReservationByRoomsDto("101A", 1));
        ReservationByDateDTO dto = new ReservationByDateDTO(map);

        String result = dto.toString();

        assertThat(result)
                .contains("ReservationByDateDTO")
                .contains(date.toString())
                .contains("101A");
    }
}
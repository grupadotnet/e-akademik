package pl.grupanet.eakademik.reservation.services.dto;

import org.commons.time.TimeSlot;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.user.dto.ResidentIdDto;
import pl.grupanet.eakademik.user.dto.StaffIdDto;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationByRoomsDTOTest {

    private ReservationDTO createReservationDto(String roomNumber, int slotNumber) {
        return new ReservationDTO(
                roomNumber,
                new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0)),
                slotNumber,
                ReservationStatus.values()[0],
                new ResidentIdDto(UUID.randomUUID(), "Jan", "Kowalski", "101A"),
                new StaffIdDto(UUID.randomUUID(), "Anna", "Nowak")
        );
    }

    @Test
    void shouldCreateReservationByRoomsDtoAndReturnCorrectRoomsMap() {
        HashMap<String, List<ReservationDTO>> roomsMap = new HashMap<>();
        List<ReservationDTO> room101Reservations = List.of(
                createReservationDto("101A", 1),
                createReservationDto("101A", 2)
        );
        roomsMap.put("101A", room101Reservations);

        ReservationByRoomsDTO dto = new ReservationByRoomsDTO(roomsMap);

        assertThat(dto.rooms()).isEqualTo(roomsMap);
        assertThat(dto.rooms()).containsEntry("101A", room101Reservations);
    }

    @Test
    void shouldBeEqualToItself() {
        ReservationByRoomsDTO dto = new ReservationByRoomsDTO(new HashMap<>());

        assertThat(dto).isEqualTo(dto);
    }

    @Test
    void shouldBeEqualWhenRoomsMapsContainSameEntries() {
        ReservationDTO reservation = createReservationDto("101A", 1);

        HashMap<String, List<ReservationDTO>> map1 = new HashMap<>();
        map1.put("101A", List.of(reservation));

        HashMap<String, List<ReservationDTO>> map2 = new HashMap<>();
        map2.put("101A", List.of(reservation));

        ReservationByRoomsDTO dto1 = new ReservationByRoomsDTO(map1);
        ReservationByRoomsDTO dto2 = new ReservationByRoomsDTO(map2);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto2).isEqualTo(dto1);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldBeEqualWhenRoomsIsNull() {
        ReservationByRoomsDTO dto1 = new ReservationByRoomsDTO(null);
        ReservationByRoomsDTO dto2 = new ReservationByRoomsDTO(null);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenRoomsMapDiffers() {
        HashMap<String, List<ReservationDTO>> map1 = new HashMap<>();
        map1.put("101A", List.of(createReservationDto("101A", 1)));

        HashMap<String, List<ReservationDTO>> map2 = new HashMap<>();
        map2.put("202B", List.of(createReservationDto("202B", 1)));

        ReservationByRoomsDTO dto1 = new ReservationByRoomsDTO(map1);
        ReservationByRoomsDTO dto2 = new ReservationByRoomsDTO(map2);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        ReservationByRoomsDTO dto = new ReservationByRoomsDTO(new HashMap<>());

        assertThat(dto).isNotEqualTo(null);
        assertThat(dto).isNotEqualTo("sth");
    }

    @Test
    void shouldContainRoomsInToString() {
        HashMap<String, List<ReservationDTO>> map = new HashMap<>();
        map.put("101A", List.of(createReservationDto("101A", 1)));
        ReservationByRoomsDTO dto = new ReservationByRoomsDTO(map);

        String result = dto.toString();

        assertThat(result)
                .contains("ReservationByRoomsDTO")
                .contains("101A");
    }
}
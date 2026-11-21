package pl.grupanet.eakademik.reservation.service.dto;

import org.commons.time.TimeSlot;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.services.dto.ReservationDTO;
import pl.grupanet.eakademik.user.dto.ResidentIdDto;
import pl.grupanet.eakademik.user.dto.StaffIdDto;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationDTOTest {

    private static final String ROOM_NUMBER = "101A";
    private static final int SLOT_NUMBER = 2;

    private TimeSlot createTimeSlot(int startHour, int endHour) {
        return new TimeSlot(
                LocalTime.of(startHour, 0),
                LocalTime.of(endHour, 0)
        );
    }

    private ResidentIdDto createResident(UUID id) {
        return new ResidentIdDto(id, "Jan", "Kowalski", "101A");
    }

    private StaffIdDto createStaff(UUID id) {
        return new StaffIdDto(id, "Anna", "Nowak");
    }

    private ReservationStatus firstStatus() {
        return ReservationStatus.values()[0];
    }

    private ReservationStatus secondStatus() {
        ReservationStatus[] values = ReservationStatus.values();
        return values.length > 1 ? values[1] : null;
    }

    @Test
    void shouldCreateReservationDtoAndReturnCorrectFieldValues() {
        TimeSlot timeSlot = createTimeSlot(10, 12);
        ResidentIdDto resident = createResident(UUID.randomUUID());
        StaffIdDto staff = createStaff(UUID.randomUUID());
        ReservationStatus status = firstStatus();

        ReservationDTO dto = new ReservationDTO(ROOM_NUMBER, timeSlot, SLOT_NUMBER, status, resident, staff);

        assertThat(dto.reservedRoomNumber()).isEqualTo(ROOM_NUMBER);
        assertThat(dto.timeSlot()).isEqualTo(timeSlot);
        assertThat(dto.slotNumber()).isEqualTo(SLOT_NUMBER);
        assertThat(dto.reservedBy()).isEqualTo(resident);
        assertThat(dto.approvedBy()).isEqualTo(staff);
        assertThat(dto.status()).isEqualTo(status);
    }

    @Test
    void shouldBeEqualToItself() {
        ReservationDTO dto = new ReservationDTO(
                ROOM_NUMBER,
                createTimeSlot(10, 12),
                SLOT_NUMBER,
                firstStatus(),
                createResident(UUID.randomUUID()),
                createStaff(UUID.randomUUID())
        );

        assertThat(dto).isEqualTo(dto);
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreTheSame() {
        TimeSlot timeSlot = createTimeSlot(10, 12);
        UUID residentId = UUID.randomUUID();
        UUID staffId = UUID.randomUUID();
        ReservationStatus status = firstStatus();

        ReservationDTO dto1 = new ReservationDTO(
                ROOM_NUMBER, timeSlot, SLOT_NUMBER, status, createResident(residentId), createStaff(staffId)
        );
        ReservationDTO dto2 = new ReservationDTO(
                ROOM_NUMBER, timeSlot, SLOT_NUMBER, status, createResident(residentId), createStaff(staffId)
        );

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto2).isEqualTo(dto1);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldBeEqualWhenNullableFieldsAreNull() {
        ReservationDTO dto1 = new ReservationDTO(null, null, 0, null, null, null);
        ReservationDTO dto2 = new ReservationDTO(null, null, 0, null, null, null);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenReservedRoomNumberDiffers() {
        TimeSlot timeSlot = createTimeSlot(10, 12);
        ResidentIdDto resident = createResident(UUID.randomUUID());
        StaffIdDto staff = createStaff(UUID.randomUUID());
        ReservationStatus status = firstStatus();

        ReservationDTO dto1 = new ReservationDTO("101A", timeSlot, SLOT_NUMBER, status, resident, staff);
        ReservationDTO dto2 = new ReservationDTO("202B", timeSlot, SLOT_NUMBER, status, resident, staff);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenTimeSlotDiffers() {
        ResidentIdDto resident = createResident(UUID.randomUUID());
        StaffIdDto staff = createStaff(UUID.randomUUID());
        ReservationStatus status = firstStatus();

        ReservationDTO dto1 = new ReservationDTO(ROOM_NUMBER, createTimeSlot(10, 12), SLOT_NUMBER, status, resident, staff);
        ReservationDTO dto2 = new ReservationDTO(ROOM_NUMBER, createTimeSlot(12, 14), SLOT_NUMBER, status, resident, staff);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenSlotNumberDiffers() {
        TimeSlot timeSlot = createTimeSlot(10, 12);
        ResidentIdDto resident = createResident(UUID.randomUUID());
        StaffIdDto staff = createStaff(UUID.randomUUID());
        ReservationStatus status = firstStatus();

        ReservationDTO dto1 = new ReservationDTO(ROOM_NUMBER, timeSlot, 1, status, resident, staff);
        ReservationDTO dto2 = new ReservationDTO(ROOM_NUMBER, timeSlot, 2, status, resident, staff);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenReservedByDiffers() {
        TimeSlot timeSlot = createTimeSlot(10, 12);
        StaffIdDto staff = createStaff(UUID.randomUUID());
        ReservationStatus status = firstStatus();

        ReservationDTO dto1 = new ReservationDTO(ROOM_NUMBER, timeSlot, SLOT_NUMBER, status, createResident(UUID.randomUUID()), staff);
        ReservationDTO dto2 = new ReservationDTO(ROOM_NUMBER, timeSlot, SLOT_NUMBER, status, createResident(UUID.randomUUID()), staff);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenApprovedByDiffers() {
        TimeSlot timeSlot = createTimeSlot(10, 12);
        ResidentIdDto resident = createResident(UUID.randomUUID());
        ReservationStatus status = firstStatus();

        ReservationDTO dto1 = new ReservationDTO(ROOM_NUMBER, timeSlot, SLOT_NUMBER, status, resident, createStaff(UUID.randomUUID()));
        ReservationDTO dto2 = new ReservationDTO(ROOM_NUMBER, timeSlot, SLOT_NUMBER, status, resident, createStaff(UUID.randomUUID()));

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualWhenStatusDiffers() {
        TimeSlot timeSlot = createTimeSlot(10, 12);
        ResidentIdDto resident = createResident(UUID.randomUUID());
        StaffIdDto staff = createStaff(UUID.randomUUID());

        ReservationDTO dto1 = new ReservationDTO(ROOM_NUMBER, timeSlot, SLOT_NUMBER, firstStatus(), resident, staff);
        ReservationDTO dto2 = new ReservationDTO(ROOM_NUMBER, timeSlot, SLOT_NUMBER, secondStatus(), resident, staff);

        assertThat(dto1).isNotEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        ReservationDTO dto = new ReservationDTO(
                ROOM_NUMBER,
                createTimeSlot(10, 12),
                SLOT_NUMBER,
                firstStatus(),
                createResident(UUID.randomUUID()),
                createStaff(UUID.randomUUID())
        );

        assertThat(dto).isNotEqualTo(null);
        assertThat(dto).isNotEqualTo("sth");
    }

    @Test
    void shouldContainAllFieldsInToString() {
        TimeSlot timeSlot = createTimeSlot(10, 12);
        ResidentIdDto resident = createResident(UUID.randomUUID());
        StaffIdDto staff = createStaff(UUID.randomUUID());
        ReservationStatus status = firstStatus();

        ReservationDTO dto = new ReservationDTO(ROOM_NUMBER, timeSlot, SLOT_NUMBER, status, resident, staff);

        String result = dto.toString();

        assertThat(result)
                .contains("ReservationDTO")
                .contains(ROOM_NUMBER)
                .contains(timeSlot.toString())
                .contains(String.valueOf(SLOT_NUMBER))
                .contains(resident.toString())
                .contains(staff.toString())
                .contains(status.toString());
    }
}
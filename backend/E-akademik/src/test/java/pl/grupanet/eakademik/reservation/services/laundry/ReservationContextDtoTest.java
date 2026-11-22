package pl.grupanet.eakademik.reservation.services.laundry;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.time.TimeSlot;
import org.commons.users.ResidentId;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.ReservationId;
import pl.grupanet.eakademik.user.dto.ResidentDto;

import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationContextDtoTest {
    private DormitoryId createDormitoryId() {
        return new DormitoryId.Builder(UniversityNames.POLITECHNIKA_KRAKOWSKA, "dor1").build();
    }


    private ResidentDto createResidentDto() {
        return new ResidentDto(
                new ResidentId(UUID.randomUUID()),
                new Name("John Doe"),
                new Name("dd"),
                createDormitoryId(),
                new RoomNumber("A33")
        );
    }

    private ReservationId createReservationId(String roomNumber, int slotNumber) {
        return new ReservationId(
                new DormitoryId.Builder(UniversityNames.POLITECHNIKA_KRAKOWSKA, "dor1").build(),
                new RoomNumber(roomNumber),
                new SlotNumber(slotNumber)
        );
    }

    private TimeSlot createTimeSlot(int startHour, int endHour) {
        return new TimeSlot(
                LocalTime.of(startHour, 0),
                LocalTime.of(endHour, 0)
        );
    }

    @Test
    void shouldCreateReservationContextAndReturnCorrectFieldValues() {
        ResidentDto residentDto = createResidentDto();
        ReservationId reservationId = createReservationId("101A", 1);
        TimeSlot timeSlot = createTimeSlot(10, 12);

        ReservationContextDto context = new ReservationContextDto(residentDto, reservationId, timeSlot);

        assertThat(context.residentDto()).isEqualTo(residentDto);
        assertThat(context.reservationId()).isEqualTo(reservationId);
        assertThat(context.timeSlot()).isEqualTo(timeSlot);
    }

    @Test
    void shouldBeEqualToItself() {
        ReservationContextDto context = new ReservationContextDto(
                createResidentDto(),
                createReservationId("101A", 1),
                createTimeSlot(10, 12)
        );

        assertThat(context).isEqualTo(context);
    }

    @Test
    void shouldBeEqualWhenAllFieldsAreTheSame() {
        ResidentDto residentDto = createResidentDto();
        DormitoryId dormitoryId = new DormitoryId.Builder(UniversityNames.POLITECHNIKA_KRAKOWSKA, "dor1").build();
        ReservationId reservationId1 = new ReservationId(dormitoryId, new RoomNumber("101A"), new SlotNumber(1));
        ReservationId reservationId2 = new ReservationId(dormitoryId, new RoomNumber("101A"), new SlotNumber(1));
        TimeSlot timeSlot1 = createTimeSlot(10, 12);
        TimeSlot timeSlot2 = createTimeSlot(10, 12);

        ReservationContextDto context1 = new ReservationContextDto(residentDto, reservationId1, timeSlot1);
        ReservationContextDto context2 = new ReservationContextDto(residentDto, reservationId2, timeSlot2);

        // when & then
        assertThat(context1).isEqualTo(context2);
        assertThat(context2).isEqualTo(context1);
        assertThat(context1.hashCode()).isEqualTo(context2.hashCode());
    }

    @Test
    void shouldBeEqualWhenFieldsAreNull() {
        ReservationContextDto context1 = new ReservationContextDto(null, null, null);
        ReservationContextDto context2 = new ReservationContextDto(null, null, null);

        assertThat(context1).isEqualTo(context2);
        assertThat(context1.hashCode()).isEqualTo(context2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenResidentDtoDiffers() {
        ReservationId reservationId = createReservationId("101A", 1);
        TimeSlot timeSlot = createTimeSlot(10, 12);

        ReservationContextDto context1 = new ReservationContextDto(createResidentDto(), reservationId, timeSlot);
        ReservationContextDto context2 = new ReservationContextDto(createResidentDto(), reservationId, timeSlot);

        assertThat(context1).isNotEqualTo(context2);
    }

    @Test
    void shouldNotBeEqualWhenReservationIdDiffers() {
        ResidentDto residentDto = createResidentDto();
        TimeSlot timeSlot = createTimeSlot(10, 12);

        ReservationContextDto context1 = new ReservationContextDto(residentDto, createReservationId("101A", 1), timeSlot);
        ReservationContextDto context2 = new ReservationContextDto(residentDto, createReservationId("202B", 2), timeSlot);

        assertThat(context1).isNotEqualTo(context2);
    }

    @Test
    void shouldNotBeEqualWhenTimeSlotDiffers() {
        ResidentDto residentDto = createResidentDto();
        ReservationId reservationId = createReservationId("101A", 1);

        ReservationContextDto context1 = new ReservationContextDto(residentDto, reservationId, createTimeSlot(10, 12));
        ReservationContextDto context2 = new ReservationContextDto(residentDto, reservationId, createTimeSlot(12, 14));

        assertThat(context1).isNotEqualTo(context2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentType() {
        ReservationContextDto context = new ReservationContextDto(
                createResidentDto(),
                createReservationId("101A", 1),
                createTimeSlot(10, 12)
        );

        assertThat(context).isNotEqualTo(null);
        assertThat(context).isNotEqualTo("sth");
    }

    @Test
    void shouldContainAllFieldsInToString() {
        ResidentDto residentDto = createResidentDto();
        ReservationId reservationId = createReservationId("101A", 1);
        TimeSlot timeSlot = createTimeSlot(10, 12);

        ReservationContextDto context = new ReservationContextDto(residentDto, reservationId, timeSlot);

        String result = context.toString();

        assertThat(result)
                .contains("ReservationContext")
                .contains(residentDto.toString())
                .contains(reservationId.toString())
                .contains(timeSlot.toString());
    }
}
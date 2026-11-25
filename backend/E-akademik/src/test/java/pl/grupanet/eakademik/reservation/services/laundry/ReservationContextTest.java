package pl.grupanet.eakademik.reservation.services.laundry;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.time.TimeSlot;
import org.commons.users.ResidentId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;
import pl.grupanet.eakademik.dormitory.repositories.DormitoryRepository;
import pl.grupanet.eakademik.dormitory.slots.SlotsCalculator;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.ReservationId;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationContextTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private DormitoryRepository dormitoryRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DormitorySpecification dormitorySpecification;

    @Mock
    private SlotsCalculator slotsCalculator;

    private ReservationContext reservationContext;

    @BeforeEach
    void setUp() {
        reservationContext = new ReservationContext(userProfileRepository, dormitoryRepository);
    }

    private DormitoryId createDormitoryId() {
        return new DormitoryId(UniversityNames.values()[0], new Name("DS1"));
    }

    private ResidentDto createResidentDto(ResidentId residentId, DormitoryId dormitoryId) {
        try {
            Constructor<?> constructor = ResidentDto.class.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i].equals(ResidentId.class)) {
                    args[i] = residentId;
                } else if (paramTypes[i].equals(DormitoryId.class)) {
                    args[i] = dormitoryId;
                } else if (paramTypes[i].equals(String.class)) {
                    args[i] = "test";
                } else if (paramTypes[i].equals(RoomNumber.class)) {
                    args[i] = new RoomNumber("101A");
                } else if (paramTypes[i].equals(Name.class)) {
                    args[i] = new Name("Jan");
                }
            }
            return (ResidentDto) constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create ResidentDto", e);
        }
    }

    @Test
    void shouldThrowExceptionWhenConstructorArgumentsAreNull() {
        assertThatThrownBy(() -> new ReservationContext(null, dormitoryRepository))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> new ReservationContext(userProfileRepository, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldPrepareReservationContextFollowingExpectedFlow() {
        UUID residentUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        ResidentId expectedResidentId = new ResidentId(residentUuid);
        ResidentDto residentDto = createResidentDto(expectedResidentId, dormitoryId);

        LocalDateTime reservationDate = LocalDateTime.of(2026, 11, 21, 10, 0);
        String roomNumberRaw = "101A";
        int slotNumberRaw = 2;

        RoomNumber expectedRoomNumber = new RoomNumber(roomNumberRaw);
        SlotNumber expectedSlotNumber = new SlotNumber(slotNumberRaw);
        TimeSlot expectedTimeSlot = new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0));
        ReservationId expectedReservationId = new ReservationId(dormitoryId, expectedRoomNumber, expectedSlotNumber);

        when(userProfileRepository.findByResidentId(expectedResidentId)).thenReturn(residentDto);
        when(dormitoryRepository.get(dormitoryId)).thenReturn(dormitorySpecification);
        when(dormitorySpecification.slotsRoomSpecification()
                .getCalculatorByRoomName(expectedRoomNumber)
                .choose(reservationDate))
                .thenReturn(slotsCalculator);
        when(slotsCalculator.calculateSlotHoursById(expectedSlotNumber)).thenReturn(expectedTimeSlot);

        ReservationContextDto result = reservationContext.prepareContext(
                residentUuid, reservationDate, roomNumberRaw, slotNumberRaw
        );

        assertThat(result).isNotNull();
        assertThat(result.residentDto()).isEqualTo(residentDto);
        assertThat(result.reservationId()).isEqualTo(expectedReservationId);
        assertThat(result.timeSlot()).isEqualTo(expectedTimeSlot);

        InOrder inOrder = inOrder(userProfileRepository, dormitoryRepository, slotsCalculator);
        inOrder.verify(userProfileRepository).findByResidentId(expectedResidentId);
        inOrder.verify(dormitoryRepository).get(dormitoryId);
        inOrder.verify(slotsCalculator).calculateSlotHoursById(expectedSlotNumber);

        verifyNoMoreInteractions(userProfileRepository, dormitoryRepository, slotsCalculator);
    }

    @Test
    void shouldStopFlowAndNotCallDormitoryRepositoryWhenResidentIsNotFound() {
        UUID residentUuid = UUID.randomUUID();
        ResidentId expectedResidentId = new ResidentId(residentUuid);
        LocalDateTime reservationDate = LocalDateTime.of(2026, 11, 21, 10, 0);

        when(userProfileRepository.findByResidentId(expectedResidentId))
                .thenThrow(new IllegalStateException("Resident not found"));

        assertThatThrownBy(() -> reservationContext.prepareContext(residentUuid, reservationDate, "101A", 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Resident not found");

        verify(userProfileRepository).findByResidentId(expectedResidentId);
        verifyNoInteractions(dormitoryRepository, slotsCalculator);
    }

    @Test
    void shouldStopFlowWhenDormitoryIsNotFound() {
        UUID residentUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        ResidentId expectedResidentId = new ResidentId(residentUuid);
        ResidentDto residentDto = createResidentDto(expectedResidentId, dormitoryId);
        LocalDateTime reservationDate = LocalDateTime.of(2026, 11, 21, 10, 0);

        when(userProfileRepository.findByResidentId(expectedResidentId)).thenReturn(residentDto);
        when(dormitoryRepository.get(dormitoryId))
                .thenThrow(new IllegalStateException("Dormitory not found"));

        assertThatThrownBy(() -> reservationContext.prepareContext(residentUuid, reservationDate, "101A", 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Dormitory not found");

        verify(userProfileRepository).findByResidentId(expectedResidentId);
        verify(dormitoryRepository).get(dormitoryId);
        verifyNoInteractions(dormitorySpecification, slotsCalculator);
    }

    @Test
    void shouldStopFlowWhenCalculatorForRoomIsNotFound() {
        UUID residentUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        ResidentId expectedResidentId = new ResidentId(residentUuid);
        RoomNumber expectedRoomNumber = new RoomNumber("999Z");
        ResidentDto residentDto = createResidentDto(expectedResidentId, dormitoryId);
        LocalDateTime reservationDate = LocalDateTime.of(2026, 11, 21, 10, 0);

        when(userProfileRepository.findByResidentId(expectedResidentId)).thenReturn(residentDto);
        when(dormitoryRepository.get(dormitoryId)).thenReturn(dormitorySpecification);
        when(dormitorySpecification.slotsRoomSpecification().getCalculatorByRoomName(expectedRoomNumber))
                .thenThrow(new IllegalArgumentException("Room calculator not found"));

        assertThatThrownBy(() -> reservationContext.prepareContext(residentUuid, reservationDate, "999Z", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Room calculator not found");

        verifyNoInteractions(slotsCalculator);
    }
}
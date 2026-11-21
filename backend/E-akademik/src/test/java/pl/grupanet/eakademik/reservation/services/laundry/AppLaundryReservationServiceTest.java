package pl.grupanet.eakademik.reservation.services.laundry;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.time.TimeSlot;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
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
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.reservation.LaundryReservationRepository;
import pl.grupanet.eakademik.reservation.ReservationId;
import pl.grupanet.eakademik.reservation.dto.ReservationStatus;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.services.dto.ReservationByDateDTO;
import pl.grupanet.eakademik.reservation.services.dto.ReservationByRoomsDTO;
import pl.grupanet.eakademik.reservation.services.dto.ReservationDTO;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppLaundryReservationServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private LaundryReservationRepository laundryReservationRepository;

    @Mock
    private ReservationContext reservationContext;

    @Mock
    private DormitoryRepository dormitoryRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DormitorySpecification dormitorySpecification;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SlotsRoomSpecification slotsRoomSpecification;

    @Mock
    private SlotsCalculator slotsCalculator;

    private AppLaundryReservationService service;

    @BeforeEach
    void setUp() {
        service = new AppLaundryReservationService(
                userProfileRepository,
                laundryReservationRepository,
                reservationContext,
                dormitoryRepository
        );
    }

    private DormitoryId createDormitoryId() {
        return new DormitoryId(UniversityNames.values()[0], new Name("DS1"));
    }

    private ResidentDto createResidentDto(ResidentId residentId, DormitoryId dormitoryId) {
        return instantiateRecord(ResidentDto.class, residentId, dormitoryId);
    }

    private StaffDto createStaffDto(StaffId staffId, DormitoryId dormitoryId) {
        return instantiateRecord(StaffDto.class, staffId, dormitoryId);
    }

    private TermSnapshot createTermSnapshot(ReservationId reservationId,
                                            ResidentId ownerId,
                                            StaffId acceptedById,
                                            ReservationStatus status,
                                            LocalDate date) {
        return instantiateRecord(TermSnapshot.class, reservationId, ownerId, acceptedById, status, date);
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiateRecord(Class<T> clazz, Object... customArgs) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> type = paramTypes[i];
                for (Object customArg : customArgs) {
                    if (customArg != null && type.isAssignableFrom(customArg.getClass())) {
                        args[i] = customArg;
                        break;
                    }
                }
                if (args[i] == null) {
                    if (type.equals(String.class)) args[i] = "test";
                    else if (type.equals(RoomNumber.class)) args[i] = new RoomNumber("101A");
                    else if (type.equals(Name.class)) args[i] = new Name("Jan");
                    else if (type.equals(int.class) || type.equals(Integer.class)) args[i] = 1;
                    else if (type.equals(boolean.class) || type.equals(Boolean.class)) args[i] = false;
                }
            }
            return (T) constructor.newInstance(args);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create instance of " + clazz.getSimpleName(), e);
        }
    }

    @Test
    void shouldReserveSlotAndReturnDtoWithoutApprovedBy() {
        UUID residentUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        ResidentId residentId = new ResidentId(residentUuid);
        ResidentDto residentDto = createResidentDto(residentId, dormitoryId);
        RoomNumber roomNumber = new RoomNumber("101A");
        SlotNumber slotNumber = new SlotNumber(1);
        ReservationId reservationId = new ReservationId(dormitoryId, roomNumber, slotNumber);
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0));
        LocalDateTime dateTime = LocalDateTime.of(2026, 11, 21, 10, 0);
        ReservationStatus expectedStatus = ReservationStatus.values()[0];

        ReservationContextDto contextDto = new ReservationContextDto(residentDto, reservationId, timeSlot);
        TermSnapshot snapshot = createTermSnapshot(reservationId, residentId, null, expectedStatus, dateTime.toLocalDate());

        when(reservationContext.prepareContext(residentUuid, dateTime, "101A", 1)).thenReturn(contextDto);
        when(laundryReservationRepository.reserve(reservationId, residentDto.id(), dateTime.toLocalDate()))
                .thenReturn(snapshot);

        ReservationDTO result = service.reserve(residentUuid, dateTime, "101A", 1);

        assertThat(result.reservedRoomNumber()).isEqualTo(roomNumber.toString());
        assertThat(result.timeSlot()).isEqualTo(timeSlot);
        assertThat(result.slotNumber()).isEqualTo(1);
        assertThat(result.status()).isEqualTo(expectedStatus);
        assertThat(result.reservedBy()).isEqualTo(residentDto.toCommonDto());
        assertThat(result.approvedBy()).isNull();

        InOrder inOrder = inOrder(reservationContext, laundryReservationRepository);
        inOrder.verify(reservationContext).prepareContext(residentUuid, dateTime, "101A", 1);
        inOrder.verify(laundryReservationRepository).reserve(reservationId, residentDto.id(), dateTime.toLocalDate());
        verifyNoMoreInteractions(reservationContext, laundryReservationRepository);
        verifyNoInteractions(userProfileRepository, dormitoryRepository);
    }

    @Test
    void shouldAcceptReservationAndIncludeStaffInDto() {
        UUID residentUuid = UUID.randomUUID();
        UUID staffUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        ResidentId residentId = new ResidentId(residentUuid);
        StaffId staffId = new StaffId(staffUuid);
        ResidentDto residentDto = createResidentDto(residentId, dormitoryId);
        StaffDto staffDto = createStaffDto(staffId, dormitoryId);
        RoomNumber roomNumber = new RoomNumber("101A");
        SlotNumber slotNumber = new SlotNumber(2);
        ReservationId reservationId = new ReservationId(dormitoryId, roomNumber, slotNumber);
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(12, 0), LocalTime.of(14, 0));
        LocalDateTime dateTime = LocalDateTime.of(2026, 11, 21, 12, 0);
        ReservationStatus expectedStatus = ReservationStatus.values()[0];

        ReservationContextDto contextDto = new ReservationContextDto(residentDto, reservationId, timeSlot);
        TermSnapshot snapshot = createTermSnapshot(reservationId, residentId, staffId, expectedStatus, dateTime.toLocalDate());

        when(reservationContext.prepareContext(residentUuid, dateTime, "101A", 2)).thenReturn(contextDto);
        when(userProfileRepository.findByStaffId(staffId)).thenReturn(staffDto);
        when(laundryReservationRepository.accept(reservationId, residentDto.id(), staffDto.id(), dateTime.toLocalDate()))
                .thenReturn(snapshot);

        ReservationDTO result = service.accept(residentUuid, staffUuid, dateTime, "101A", 2);

        assertThat(result.reservedRoomNumber()).isEqualTo(roomNumber.toString());
        assertThat(result.slotNumber()).isEqualTo(2);
        assertThat(result.reservedBy()).isEqualTo(residentDto.toCommonDto());
        assertThat(result.approvedBy()).isEqualTo(staffDto.toCommonDto());

        InOrder inOrder = inOrder(reservationContext, userProfileRepository, laundryReservationRepository);
        inOrder.verify(reservationContext).prepareContext(residentUuid, dateTime, "101A", 2);
        inOrder.verify(userProfileRepository).findByStaffId(staffId);
        inOrder.verify(laundryReservationRepository).accept(reservationId, residentDto.id(), staffDto.id(), dateTime.toLocalDate());
    }

    @Test
    void shouldCancelByStaffAndReturnDtoWithStaffInfo() {
        UUID residentUuid = UUID.randomUUID();
        UUID staffUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        ResidentId residentId = new ResidentId(residentUuid);
        StaffId staffId = new StaffId(staffUuid);
        ResidentDto residentDto = createResidentDto(residentId, dormitoryId);
        StaffDto staffDto = createStaffDto(staffId, dormitoryId);
        RoomNumber roomNumber = new RoomNumber("101A");
        SlotNumber slotNumber = new SlotNumber(1);
        ReservationId reservationId = new ReservationId(dormitoryId, roomNumber, slotNumber);
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0));
        LocalDateTime dateTime = LocalDateTime.of(2026, 11, 21, 10, 0);
        ReservationStatus expectedStatus = ReservationStatus.values()[0];

        ReservationContextDto contextDto = new ReservationContextDto(residentDto, reservationId, timeSlot);
        TermSnapshot snapshot = createTermSnapshot(reservationId, residentId, staffId, expectedStatus, dateTime.toLocalDate());

        when(reservationContext.prepareContext(residentUuid, dateTime, "101A", 1)).thenReturn(contextDto);
        when(userProfileRepository.findByStaffId(staffId)).thenReturn(staffDto);
        when(laundryReservationRepository.cancelByStaff(reservationId, residentDto.id(), staffDto.id(), dateTime.toLocalDate()))
                .thenReturn(snapshot);

        ReservationDTO result = service.cancelByStaff(residentUuid, staffUuid, dateTime, "101A", 1);

        assertThat(result.approvedBy()).isEqualTo(staffDto.toCommonDto());
        verify(laundryReservationRepository).cancelByStaff(reservationId, residentDto.id(), staffDto.id(), dateTime.toLocalDate());
    }

    @Test
    void shouldCancelByOwnerAndReturnDtoWithoutStaffInfo() {
        UUID residentUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        ResidentId residentId = new ResidentId(residentUuid);
        ResidentDto residentDto = createResidentDto(residentId, dormitoryId);
        RoomNumber roomNumber = new RoomNumber("101A");
        SlotNumber slotNumber = new SlotNumber(1);
        ReservationId reservationId = new ReservationId(dormitoryId, roomNumber, slotNumber);
        TimeSlot timeSlot = new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0));
        LocalDateTime dateTime = LocalDateTime.of(2026, 11, 21, 10, 0);
        ReservationStatus expectedStatus = ReservationStatus.values()[0];

        ReservationContextDto contextDto = new ReservationContextDto(residentDto, reservationId, timeSlot);
        TermSnapshot snapshot = createTermSnapshot(reservationId, residentId, null, expectedStatus, dateTime.toLocalDate());

        when(reservationContext.prepareContext(residentUuid, dateTime, "101A", 1)).thenReturn(contextDto);
        when(laundryReservationRepository.cancelByOwner(reservationId, residentDto.id(), dateTime.toLocalDate()))
                .thenReturn(snapshot);

        ReservationDTO result = service.cancelByOwner(residentUuid, dateTime, "101A", 1);

        assertThat(result.approvedBy()).isNull();
        verify(laundryReservationRepository).cancelByOwner(reservationId, residentDto.id(), dateTime.toLocalDate());
        verifyNoInteractions(userProfileRepository);
    }

    @Test
    void shouldFindReservationsForSingleRoomAndFilterOtherResidentsWhenFinderIsResident() {
        UUID finderUuid = UUID.randomUUID();
        UUID otherResidentUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        ResidentId finderId = new ResidentId(finderUuid);
        ResidentId otherResidentId = new ResidentId(otherResidentUuid);

        ResidentDto finderDto = createResidentDto(finderId, dormitoryId);

        RoomNumber roomNumber = new RoomNumber("101A");
        SlotNumber slot1 = new SlotNumber(1);
        SlotNumber slot2 = new SlotNumber(2);
        SlotNumber slot3 = new SlotNumber(3);

        LocalDateTime dateTime = LocalDateTime.of(2026, 11, 21, 10, 0);
        LocalDate date = dateTime.toLocalDate();
        List<ReservationStatus> allStatuses = List.of(ReservationStatus.values());

        ReservationId resId1 = new ReservationId(dormitoryId, roomNumber, slot1);
        ReservationId resId2 = new ReservationId(dormitoryId, roomNumber, slot2);

        ReservationStatus occupiedStatus = ReservationStatus.values()[0];
        TermSnapshot snap1 = createTermSnapshot(resId1, finderId, null, occupiedStatus, date);
        TermSnapshot snap2 = createTermSnapshot(resId2, otherResidentId, null, occupiedStatus, date);

        when(userProfileRepository.findByResidentId(finderId)).thenReturn(finderDto);
        when(userProfileRepository.findByResidentIds(List.of(finderId))).thenReturn(Map.of(finderId, finderDto));
        when(dormitoryRepository.get(dormitoryId)).thenReturn(dormitorySpecification);
        when(dormitorySpecification.slotsRoomSpecification().getCalculatorByRoomName(roomNumber).choose(dateTime))
                .thenReturn(slotsCalculator);
        when(slotsCalculator.generateSlotIds()).thenReturn(List.of(slot1, slot2, slot3));
        when(slotsCalculator.calculateSlotHoursById(any())).thenReturn(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0)));

        when(laundryReservationRepository.findAllTermsForSlotAndDate(
                dormitoryId, List.of(slot1, slot2, slot3), allStatuses, List.of(date)
        )).thenReturn(List.of(snap1, snap2));

        List<ReservationDTO> results = service.findReservations(finderUuid, dateTime, "101A", allStatuses);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(ReservationDTO::slotNumber).containsExactly(1, 3);
        verify(userProfileRepository).findByResidentIds(List.of(finderId));
        verify(userProfileRepository, never()).findByResidentId(otherResidentId);
        verifyNoMoreInteractions(userProfileRepository);
    }

    @Test
    void shouldReturnAllReservationsWithoutFilteringWhenFinderIsStaff() {
        UUID staffUuid = UUID.randomUUID();
        UUID resident1Uuid = UUID.randomUUID();
        UUID resident2Uuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();

        StaffId staffId = new StaffId(staffUuid);
        ResidentId resident1Id = new ResidentId(resident1Uuid);
        ResidentId resident2Id = new ResidentId(resident2Uuid);

        StaffDto staffDto = createStaffDto(staffId, dormitoryId);
        ResidentDto resident1Dto = createResidentDto(resident1Id, dormitoryId);
        ResidentDto resident2Dto = createResidentDto(resident2Id, dormitoryId);

        RoomNumber roomNumber = new RoomNumber("101A");
        SlotNumber slot1 = new SlotNumber(1);
        SlotNumber slot2 = new SlotNumber(2);
        LocalDateTime dateTime = LocalDateTime.of(2026, 11, 21, 10, 0);
        LocalDate date = dateTime.toLocalDate();
        List<ReservationStatus> allStatuses = List.of(ReservationStatus.values());

        ReservationStatus occupiedStatus = ReservationStatus.values()[0];
        TermSnapshot snap1 = createTermSnapshot(new ReservationId(dormitoryId, roomNumber, slot1), resident1Id, staffId, occupiedStatus, date);
        TermSnapshot snap2 = createTermSnapshot(new ReservationId(dormitoryId, roomNumber, slot2), resident2Id, null, occupiedStatus, date);

        when(userProfileRepository.findByResidentId(new ResidentId(staffUuid)))
                .thenThrow(new IllegalStateException("Not a resident"));
        when(userProfileRepository.findByStaffId(staffId)).thenReturn(staffDto);
        when(userProfileRepository.findByResidentIds(List.of(resident1Id, resident2Id)))
                .thenReturn(Map.of(resident1Id, resident1Dto, resident2Id, resident2Dto));
        when(userProfileRepository.findByStaffIds(List.of(staffId)))
                .thenReturn(Map.of(staffId, staffDto));

        when(dormitoryRepository.get(dormitoryId)).thenReturn(dormitorySpecification);
        when(dormitorySpecification.slotsRoomSpecification().getCalculatorByRoomName(roomNumber).choose(dateTime))
                .thenReturn(slotsCalculator);
        when(slotsCalculator.generateSlotIds()).thenReturn(List.of(slot1, slot2));
        when(slotsCalculator.calculateSlotHoursById(any())).thenReturn(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0)));

        when(laundryReservationRepository.findAllTermsForSlotAndDate(
                dormitoryId, List.of(slot1, slot2), allStatuses, List.of(date)
        )).thenReturn(List.of(snap1, snap2));

        List<ReservationDTO> results = service.findReservations(staffUuid, dateTime, "101A", allStatuses);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(ReservationDTO::slotNumber).containsExactly(1, 2);
        verify(userProfileRepository, times(1)).findByStaffId(staffId);
        verify(userProfileRepository, times(1)).findByResidentIds(List.of(resident1Id, resident2Id));
        verify(userProfileRepository, times(1)).findByStaffIds(List.of(staffId));
    }

    @Test
    void shouldFindReservationsGroupedByRoomsAndDates() {
        UUID staffUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        StaffId staffId = new StaffId(staffUuid);
        StaffDto staffDto = createStaffDto(staffId, dormitoryId);

        RoomNumber room1 = new RoomNumber("101A");
        RoomNumber room2 = new RoomNumber("202B");
        SlotNumber slot1 = new SlotNumber(1);
        LocalDateTime day1 = LocalDateTime.of(2026, 11, 21, 10, 0);
        LocalDateTime day2 = LocalDateTime.of(2026, 11, 22, 10, 0);
        List<ReservationStatus> allStatuses = List.of(ReservationStatus.values());

        when(userProfileRepository.findByResidentId(new ResidentId(staffUuid)))
                .thenThrow(new IllegalStateException("Not a resident"));
        when(userProfileRepository.findByStaffId(staffId)).thenReturn(staffDto);
        when(dormitoryRepository.get(dormitoryId)).thenReturn(dormitorySpecification);
        when(dormitorySpecification.slotsRoomSpecification()).thenReturn(slotsRoomSpecification);
        when(slotsRoomSpecification.getRooms()).thenReturn(List.of(room1, room2));
        when(slotsRoomSpecification.getCalculatorByRoomName(any()).choose(any())).thenReturn(slotsCalculator);
        when(slotsCalculator.generateSlotIds()).thenReturn(List.of(slot1));
        when(slotsCalculator.calculateSlotHoursById(slot1)).thenReturn(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0)));
        when(laundryReservationRepository.findAllTermsForSlotAndDate(eq(dormitoryId), any(), eq(allStatuses), any()))
                .thenReturn(List.of());

        ReservationByDateDTO result = service.findReservations(staffUuid, List.of(day1, day2), allStatuses);

        assertThat(result.dates()).containsOnlyKeys(day1.toLocalDate(), day2.toLocalDate());
        ReservationByRoomsDTO day1Rooms = result.dates().get(day1.toLocalDate());
        assertThat(day1Rooms.rooms()).containsOnlyKeys(room1.toString(), room2.toString());

        verify(userProfileRepository, times(1)).findByStaffId(staffId);
        verify(dormitoryRepository, times(1)).get(dormitoryId);
        verify(userProfileRepository, never()).findByResidentIds(any());
        verify(userProfileRepository, never()).findByStaffIds(any());
    }

    @Test
    void shouldFindReservationsGroupedByRoomsForSingleDate() {
        UUID residentUuid = UUID.randomUUID();
        UUID otherResidentUuid = UUID.randomUUID();
        DormitoryId dormitoryId = createDormitoryId();
        ResidentId residentId = new ResidentId(residentUuid);
        ResidentId otherResidentId = new ResidentId(otherResidentUuid);

        ResidentDto residentDto = createResidentDto(residentId, dormitoryId);

        RoomNumber room1 = new RoomNumber("101A");
        RoomNumber room2 = new RoomNumber("202B");
        SlotNumber slot1 = new SlotNumber(1);
        SlotNumber slot2 = new SlotNumber(2);
        LocalDateTime dateTime = LocalDateTime.of(2026, 11, 21, 10, 0);
        LocalDate date = dateTime.toLocalDate();
        List<ReservationStatus> allStatuses = List.of(ReservationStatus.values());

        ReservationStatus occupiedStatus = ReservationStatus.values()[0];
        TermSnapshot ownSnap = createTermSnapshot(new ReservationId(dormitoryId, room1, slot1), residentId, null, occupiedStatus, date);
        TermSnapshot otherSnap = createTermSnapshot(new ReservationId(dormitoryId, room2, slot2), otherResidentId, null, occupiedStatus, date);

        when(userProfileRepository.findByResidentId(residentId)).thenReturn(residentDto);
        when(userProfileRepository.findByResidentIds(List.of(residentId))).thenReturn(Map.of(residentId, residentDto));
        when(dormitoryRepository.get(dormitoryId)).thenReturn(dormitorySpecification);
        when(dormitorySpecification.slotsRoomSpecification()).thenReturn(slotsRoomSpecification);
        when(slotsRoomSpecification.getRooms()).thenReturn(List.of(room1, room2));
        when(slotsRoomSpecification.getCalculatorByRoomName(any()).choose(dateTime)).thenReturn(slotsCalculator);
        when(slotsCalculator.generateSlotIds()).thenReturn(List.of(slot1, slot2));
        when(slotsCalculator.calculateSlotHoursById(any())).thenReturn(new TimeSlot(LocalTime.of(10, 0), LocalTime.of(12, 0)));
        when(laundryReservationRepository.findAllTermsForSlotAndDate(eq(dormitoryId), any(), eq(allStatuses), eq(List.of(date))))
                .thenReturn(List.of(ownSnap, otherSnap));

        ReservationByRoomsDTO result = service.findReservations(residentUuid, dateTime, allStatuses);

        assertThat(result.rooms()).containsOnlyKeys(room1.toString(), room2.toString());
        assertThat(result.rooms().get(room1.toString()))
                .extracting(ReservationDTO::slotNumber)
                .containsExactly(1, 2);
        assertThat(result.rooms().get(room2.toString()))
                .extracting(ReservationDTO::slotNumber)
                .containsExactly(1);

        verify(userProfileRepository, times(1)).findByResidentId(residentId);
        verify(userProfileRepository, times(1)).findByResidentIds(List.of(residentId));
        verify(userProfileRepository, never()).findByResidentId(otherResidentId);
        verify(dormitoryRepository, times(1)).get(dormitoryId);
    }
}
package pl.grupanet.eakademik.reservation.services.laundry;

import lombok.NonNull;
import org.commons.DormitoryId;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.time.TimeSlot;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;
import pl.grupanet.eakademik.dormitory.repositories.DormitoryRepository;
import pl.grupanet.eakademik.dormitory.slots.SlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class AppLaundryReservationService implements LaundryReservationService {

    private record UserData(DormitoryId dormitoryId, boolean isStaff) {
    }

    private final UserProfileRepository userProfileRepository;
    private final LaundryReservationRepository laundryReservationRepository;
    private final ReservationContext reservationContext;
    private final DormitoryRepository dormitoryRepository;

    public AppLaundryReservationService(UserProfileRepository userProfileRepository,
                                        LaundryReservationRepository laundryReservationRepository,
                                        ReservationContext reservationContext,
                                        DormitoryRepository dormitoryRepository) {
        this.userProfileRepository = userProfileRepository;
        this.laundryReservationRepository = laundryReservationRepository;
        this.reservationContext = reservationContext;
        this.dormitoryRepository = dormitoryRepository;
    }

    @Override
    public ReservationDTO reserve(UUID residentUUID, LocalDateTime reservationDate,
                                  String roomToReserveId, int slotNumberId) {
        ReservationContextDto context = reservationContext.prepareContext(
                residentUUID, reservationDate, roomToReserveId, slotNumberId
        );

        TermSnapshot termSnapshot = laundryReservationRepository.reserve(
                context.reservationId(),
                context.residentDto().id(),
                reservationDate.toLocalDate()
        );

        return toDto(termSnapshot, context.timeSlot(), context.residentDto(), null);
    }

    @Override
    public ReservationDTO accept(UUID residentUUID, UUID staffUUID, LocalDateTime reservationDate,
                                 String roomToReserveId, int slotNumberId) {
        ReservationContextDto context = reservationContext.prepareContext(
                residentUUID, reservationDate, roomToReserveId, slotNumberId
        );
        StaffDto staffDto = userProfileRepository.findByStaffId(new StaffId(staffUUID));

        TermSnapshot termSnapshot = laundryReservationRepository.accept(
                context.reservationId(),
                context.residentDto().id(),
                staffDto.id(),
                reservationDate.toLocalDate()
        );

        return toDto(termSnapshot, context.timeSlot(), context.residentDto(), staffDto);
    }

    @Override
    public ReservationDTO cancelByStaff(UUID residentUUID, UUID staffUUID, LocalDateTime reservationDate,
                                        String roomToReserveId, int slotNumberId) {
        ReservationContextDto context = reservationContext.prepareContext(
                residentUUID, reservationDate, roomToReserveId, slotNumberId
        );
        StaffDto staffDto = userProfileRepository.findByStaffId(new StaffId(staffUUID));

        TermSnapshot termSnapshot = laundryReservationRepository.cancelByStaff(
                context.reservationId(),
                context.residentDto().id(),
                staffDto.id(),
                reservationDate.toLocalDate()
        );

        return toDto(termSnapshot, context.timeSlot(), context.residentDto(), staffDto);
    }

    @Override
    public ReservationDTO cancelByOwner(UUID residentUUID, LocalDateTime reservationDate,
                                        String roomToReserveId, int slotNumberId) {
        ReservationContextDto context = reservationContext.prepareContext(
                residentUUID, reservationDate, roomToReserveId, slotNumberId
        );

        TermSnapshot termSnapshot = laundryReservationRepository.cancelByOwner(
                context.reservationId(),
                context.residentDto().id(),
                reservationDate.toLocalDate()
        );

        return toDto(termSnapshot, context.timeSlot(), context.residentDto(), null);
    }

    @Override
    public List<ReservationDTO> findReservations(UUID finderUUID, LocalDateTime reservationDate,
                                                 String roomToReserveId, List<ReservationStatus> statuses) {
        UserData userData = resolveUserData(finderUUID);
        DormitorySpecification specification = dormitoryRepository.get(userData.dormitoryId());
        RoomNumber roomNumber = new RoomNumber(roomToReserveId);

        return findReservationsForRoom(specification, userData, finderUUID, reservationDate, roomNumber, statuses);
    }

    @Override
    public ReservationByRoomsDTO findReservations(UUID finderUUID, LocalDateTime reservationDate,
                                                  List<ReservationStatus> statuses) {
        UserData userData = resolveUserData(finderUUID);
        DormitorySpecification specification = dormitoryRepository.get(userData.dormitoryId());

        return findReservationsForAllRooms(specification, userData, finderUUID, reservationDate, statuses);
    }

    @Override
    public ReservationByDateDTO findReservations(UUID finderUUID, List<LocalDateTime> reservationDates,
                                                 List<ReservationStatus> statuses) {
        UserData userData = resolveUserData(finderUUID);
        DormitorySpecification specification = dormitoryRepository.get(userData.dormitoryId());

        HashMap<LocalDate, ReservationByRoomsDTO> dates = new HashMap<>();
        for (LocalDateTime reservationDate : reservationDates) {
            dates.put(
                    reservationDate.toLocalDate(),
                    findReservationsForAllRooms(specification, userData, finderUUID, reservationDate, statuses)
            );
        }

        return new ReservationByDateDTO(dates);
    }

    private ReservationDTO toDto(@NonNull TermSnapshot termSnapshot,
                                 @NonNull TimeSlot timeSlot,
                                 ResidentDto residentDto,
                                 StaffDto approvedBy) {
        ReservationId reservationId = termSnapshot.id();
        return new ReservationDTO(
                reservationId.roomNumber().toString(),
                timeSlot,
                reservationId.slotNumber().getNumberAsInt(),
                termSnapshot.status(),
                residentDto != null ? residentDto.toCommonDto() : null,
                approvedBy != null ? approvedBy.toCommonDto() : null
        );
    }

    private ReservationByRoomsDTO findReservationsForAllRooms(DormitorySpecification specification,
                                                              UserData userData,
                                                              UUID finderUUID,
                                                              LocalDateTime reservationDate,
                                                              List<ReservationStatus> statuses) {
        SlotsRoomSpecification slotsRoomSpecification = specification.slotsRoomSpecification();
        HashMap<String, List<ReservationDTO>> rooms = new HashMap<>();

        for (RoomNumber roomNumber : slotsRoomSpecification.getRooms()) {
            List<ReservationDTO> roomReservations = findReservationsForRoom(
                    specification, userData, finderUUID, reservationDate, roomNumber, statuses
            );
            rooms.put(roomNumber.toString(), roomReservations);
        }

        return new ReservationByRoomsDTO(rooms);
    }

    private List<ReservationDTO> findReservationsForRoom(DormitorySpecification specification,
                                                         UserData userData,
                                                         UUID finderUUID,
                                                         LocalDateTime reservationDate,
                                                         RoomNumber roomNumber,
                                                         List<ReservationStatus> statuses) {
        SlotsCalculator slotsCalculator = specification.slotsRoomSpecification()
                .getCalculatorByRoomName(roomNumber)
                .choose(reservationDate);

        List<ReservationDTO> reservationDTOs = getTermSnapshots(
                slotsCalculator,
                roomNumber,
                userData.dormitoryId(),
                slotsCalculator.generateSlotIds(),
                statuses,
                reservationDate.toLocalDate()
        );

        return filterVisibleReservations(reservationDTOs, finderUUID, userData.isStaff());
    }

    private List<ReservationDTO> filterVisibleReservations(List<ReservationDTO> reservations,
                                                           UUID finderUUID,
                                                           boolean isStaff) {
        if (isStaff) {
            return reservations;
        }
        return reservations.stream()
                .filter(dto -> dto.reservedBy() == null || dto.reservedBy().userId().equals(finderUUID))
                .toList();
    }

    private List<ReservationDTO> getTermSnapshots(@NonNull SlotsCalculator slotsCalculator,
                                                  @NonNull RoomNumber roomNumber,
                                                  @NonNull DormitoryId dormitoryId,
                                                  @NonNull List<SlotNumber> slotNumbers,
                                                  @NonNull List<ReservationStatus> statuses,
                                                  @NonNull LocalDate reservationDate) {
        List<TermSnapshot> termSnapshots = laundryReservationRepository.findAllTermsForSlotAndDate(
                dormitoryId,
                slotNumbers,
                statuses,
                List.of(reservationDate)
        );

        Map<ReservationId, TermSnapshot> termSnapshotMap = termSnapshots.stream()
                .collect(Collectors.toMap(TermSnapshot::id, Function.identity(), (first, second) -> first));

        Map<ResidentId, ResidentDto> residentsCache = new HashMap<>();
        Map<StaffId, StaffDto> staffCache = new HashMap<>();

        List<ReservationDTO> reservationDTOs = new ArrayList<>();

        for (SlotNumber slotId : slotNumbers) {
            ReservationId expectedId = new ReservationId(dormitoryId, roomNumber, slotId);
            TermSnapshot termSnapshot = termSnapshotMap.getOrDefault(
                    expectedId,
                    TermSnapshot.createOpenTerm(expectedId, reservationDate)
            );

            if (statuses.contains(termSnapshot.status())) {
                ResidentDto residentDto = termSnapshot.ownerId() != null
                        ? residentsCache.computeIfAbsent(termSnapshot.ownerId(), userProfileRepository::findByResidentId)
                        : null;
                StaffDto staffDto = termSnapshot.acceptedById() != null
                        ? staffCache.computeIfAbsent(termSnapshot.acceptedById(), userProfileRepository::findByStaffId)
                        : null;

                reservationDTOs.add(toDto(
                        termSnapshot,
                        slotsCalculator.calculateSlotHoursById(slotId),
                        residentDto,
                        staffDto
                ));
            }
        }
        return reservationDTOs;
    }

    private UserData resolveUserData(@NonNull UUID finderUUID) {
        try {
            ResidentDto residentDto = userProfileRepository.findByResidentId(new ResidentId(finderUUID));
            return new UserData(residentDto.dormitoryId(), false);
        } catch (Exception e) {
            StaffDto staffDto = userProfileRepository.findByStaffId(new StaffId(finderUUID));
            return new UserData(staffDto.dormitoryId(), true);
        }
    }

}
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
import pl.grupanet.eakademik.reservation.LaundryReservationRepository;
import pl.grupanet.eakademik.reservation.ReservationId;
import pl.grupanet.eakademik.reservation.dto.TermSnapshot;
import pl.grupanet.eakademik.reservation.services.dto.ReservationDTO;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

class AppLaundryReservationService {
    private final UserProfileRepository userProfileRepository;
    private final LaundryReservationRepository laundryReservationRepository;
    private final ReservationContext reservationContext;
    private final DormitoryRepository dormitoryRepository;

    public AppLaundryReservationService(@NonNull final UserProfileRepository userProfileRepository,
                                        @NonNull final LaundryReservationRepository laundryReservationRepository,
                                        @NonNull final ReservationContext reservationContext, DormitoryRepository dormitoryRepository) {
        this.userProfileRepository = userProfileRepository;
        this.laundryReservationRepository = laundryReservationRepository;
        this.reservationContext = reservationContext;
        this.dormitoryRepository = dormitoryRepository;
    }

    public List<ReservationDTO> findReservations(UUID finderUUID, LocalDateTime reservationDate,
                                                 String roomToReserveId, List<Integer> slotNumberIds) {


        DormitoryId dormitoryId = getDormitoryId(finderUUID);
        RoomNumber roomNumber = new RoomNumber(roomToReserveId);
        List<SlotNumber> slotNumbers = slotNumberIds.stream().map(SlotNumber::new).toList();

        DormitorySpecification dormitorySpecification = dormitoryRepository.get(dormitoryId);
        SlotsCalculator slotsCalculator = dormitorySpecification.slotsRoomSpecification()
                .getCalculatorByRoomName(roomNumber)
                .choose(reservationDate);

        return null;
    }

    private DormitoryId getDormitoryId(final @NonNull UUID finderUUID) {
        try {
            ResidentDto residentDto = userProfileRepository.findByResidentId(new ResidentId(finderUUID));
            return residentDto.dormitoryId();
        }catch (Exception e) {
            StaffDto staffDto = userProfileRepository.findByStaffId(new StaffId(finderUUID));
            return staffDto.dormitoryId();
        }
    }

    public ReservationDTO reserve(UUID residentUUID, LocalDateTime reservationDate,
                                  String roomToReserveId, int slotNumberId) {
        ReservationContextDto context = reservationContext.prepareContext(residentUUID, reservationDate,
                roomToReserveId, slotNumberId);

        TermSnapshot termSnapshot = laundryReservationRepository.reserve(
                context.reservationId(),
                context.residentDto().id(),
                reservationDate.toLocalDate()
        );

        return toDto(termSnapshot, context.timeSlot(), context.residentDto(), null);
    }

    public ReservationDTO accept(UUID residentUUID, UUID staffUUID, LocalDateTime reservationDate,
                                 String roomToReserveId, int slotNumberId) {
        ReservationContextDto context = reservationContext.prepareContext(residentUUID, reservationDate,
                roomToReserveId, slotNumberId);
        StaffDto staffDto = userProfileRepository.findByStaffId(new StaffId(staffUUID));

        TermSnapshot termSnapshot = laundryReservationRepository.accept(
                context.reservationId(),
                context.residentDto().id(),
                staffDto.id(),
                reservationDate.toLocalDate()
        );

        return toDto(termSnapshot, context.timeSlot(), context.residentDto(), staffDto);
    }

    public ReservationDTO cancelByStaff(UUID residentUUID, UUID staffUUID, LocalDateTime reservationDate,
                                        String roomToReserveId, int slotNumberId) {
        ReservationContextDto context = reservationContext.prepareContext(residentUUID, reservationDate,
                roomToReserveId, slotNumberId);
        StaffDto staffDto = userProfileRepository.findByStaffId(new StaffId(staffUUID));

        TermSnapshot termSnapshot = laundryReservationRepository.cancelByStaff(
                context.reservationId(),
                context.residentDto().id(),
                staffDto.id(),
                reservationDate.toLocalDate()
        );

        return toDto(termSnapshot, context.timeSlot(), context.residentDto(), staffDto);
    }

    public ReservationDTO cancelByOwner(UUID residentUUID, LocalDateTime reservationDate,
                                        String roomToReserveId, int slotNumberId) {
        ReservationContextDto context = reservationContext.prepareContext(residentUUID, reservationDate,
                roomToReserveId, slotNumberId);

        TermSnapshot termSnapshot = laundryReservationRepository.cancelByOwner(
                context.reservationId(),
                context.residentDto().id(),
                reservationDate.toLocalDate()
        );

        return toDto(termSnapshot, context.timeSlot(), context.residentDto(), null);
    }

    private ReservationDTO toDto(@NonNull TermSnapshot termSnapshot, @NonNull TimeSlot timeSlot,
                                 @NonNull ResidentDto residentDto, StaffDto approvedBy) {
        ReservationId reservationId = termSnapshot.id();
        return new ReservationDTO(
                reservationId.roomNumber().toString(),
                timeSlot,
                reservationId.slotNumber().getNumberAsInt(),
                termSnapshot.status(),
                residentDto.toCommonDto(),
                approvedBy != null ? approvedBy.toCommonDto() : null
        );
    }
}
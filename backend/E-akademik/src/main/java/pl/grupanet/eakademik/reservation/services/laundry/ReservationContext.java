package pl.grupanet.eakademik.reservation.services.laundry;

import lombok.NonNull;
import org.commons.RoomNumber;
import org.commons.SlotNumber;
import org.commons.time.TimeSlot;
import org.commons.users.ResidentId;
import pl.grupanet.eakademik.dormitory.DormitorySpecification;
import pl.grupanet.eakademik.dormitory.repositories.DormitoryRepository;
import pl.grupanet.eakademik.dormitory.slots.SlotsCalculator;
import pl.grupanet.eakademik.reservation.ReservationId;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;

import java.time.LocalDateTime;
import java.util.UUID;

class ReservationContext {
    private final UserProfileRepository userProfileRepository;
    private final DormitoryRepository dormitoryRepository;

    public ReservationContext(@NonNull final UserProfileRepository userProfileRepository,
                              @NonNull final DormitoryRepository dormitoryRepository) {
        this.userProfileRepository = userProfileRepository;
        this.dormitoryRepository = dormitoryRepository;
    }

    public ReservationContextDto prepareContext(UUID residentUUID, LocalDateTime reservationDate,
                                                String roomToReserveId, int slotNumberId) {
        ResidentId residentId = new ResidentId(residentUUID);
        RoomNumber roomNumber = new RoomNumber(roomToReserveId);
        SlotNumber slotNumber = new SlotNumber(slotNumberId);

        ResidentDto residentDto = userProfileRepository.findByResidentId(residentId);

        DormitorySpecification dormitorySpecification = dormitoryRepository.get(residentDto.dormitoryId());
        SlotsCalculator slotsCalculator = dormitorySpecification.slotsRoomSpecification()
                .getCalculatorByRoomName(roomNumber)
                .choose(reservationDate);
        TimeSlot timeSlot = slotsCalculator.calculateSlotHoursById(slotNumber);

        ReservationId reservationId = new ReservationId(residentDto.dormitoryId(), roomNumber, slotNumber);

        return new ReservationContextDto(residentDto, reservationId, timeSlot);
    }
}

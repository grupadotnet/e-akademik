package pl.grupanet.eakademik.user.dto;

import lombok.NonNull;
import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.RoomNumber;
import org.commons.users.ResidentId;

public record ResidentDto(
        @NonNull ResidentId id,
        @NonNull Name fullName,
        @NonNull DormitoryId dormitoryId,
        @NonNull RoomNumber roomNumber
        ) {
}

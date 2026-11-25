package pl.grupanet.eakademik.user.dto;

import lombok.NonNull;
import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.users.StaffId;

public record StaffDto(
        @NonNull StaffId id,
        @NonNull Name name,
        @NonNull Name surname,
        @NonNull DormitoryId dormitoryId
) {
    public StaffIdDto toCommonDto() {
        return new StaffIdDto(id.getIdAsUUID(), name.toString(), surname.toString());
    }
}

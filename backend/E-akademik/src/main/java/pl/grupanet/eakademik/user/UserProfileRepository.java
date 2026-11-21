package pl.grupanet.eakademik.user;

import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

import java.util.List;
import java.util.Map;

public interface UserProfileRepository {
    ResidentDto findByResidentId(@NonNull ResidentId residentId);

    Map<ResidentId, ResidentDto> findByResidentIds(@NonNull List<ResidentId> residentId);

    StaffDto findByStaffId(@NonNull StaffId staffId);

    Map<StaffId, StaffDto> findByStaffIds(@NonNull List<StaffId> staffIds);
}

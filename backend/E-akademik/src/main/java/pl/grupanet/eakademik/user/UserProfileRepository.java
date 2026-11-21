package pl.grupanet.eakademik.user;

import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

import java.util.List;

public interface UserProfileRepository {
    ResidentDto findByResidentId(@NonNull ResidentId residentId);
    List<ResidentDto> findByResidentIds(@NonNull List<ResidentId> residentId);
    StaffDto findByStaffId(@NonNull StaffId staffId);
    List<StaffDto> findByStaffIds(@NonNull List<StaffId> staffIds);
}

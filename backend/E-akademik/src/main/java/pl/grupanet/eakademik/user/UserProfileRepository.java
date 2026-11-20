package pl.grupanet.eakademik.user;

import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

public interface UserProfileRepository {
    ResidentDto findByResidentId(@NonNull ResidentId residentId);
    StaffDto findByStaffId(@NonNull StaffId staffId);
}

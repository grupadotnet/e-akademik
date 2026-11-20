package pl.grupanet.eakademik.user;

import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

import java.util.Optional;

public interface UserProfileRepository {
    ResidentDto saveResident(@NonNull ResidentDto residentDto);
    StaffDto saveStaff(@NonNull StaffDto staffDto);
    Optional<ResidentDto> findByResidentId(@NonNull ResidentId residentId);
    Optional<StaffDto> findByStaffId(@NonNull StaffId staffId);
}

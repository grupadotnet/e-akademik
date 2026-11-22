package pl.grupanet.eakademik.user;

import lombok.NonNull;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

import java.util.List;
import java.util.Map;

class AppUserProfileRepository implements UserProfileRepository {
    @Override
    public ResidentDto findByResidentId(@NonNull ResidentId residentId) {
        return null;
    }

    @Override
    public Map<ResidentId, ResidentDto> findByResidentIds(@NonNull List<ResidentId> residentId) {
        return Map.of();
    }

    @Override
    public StaffDto findByStaffId(@NonNull StaffId staffId) {
        return null;
    }

    @Override
    public Map<StaffId, StaffDto> findByStaffIds(@NonNull List<StaffId> staffIds) {
        return Map.of();
    }
}

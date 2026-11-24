package pl.grupanet.eakademik.register;

import org.commons.*;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.authentication.AuthService;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

@Service
class UserRegistrationService implements UserRegistrationRepository {

    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;

    public UserRegistrationService(UserProfileRepository userProfileRepository, AuthService authService) {
        this.userProfileRepository = userProfileRepository;
        this.authService = authService;
    }

    @Override
    @Transactional
    public void registerResident(@NonNull Email email, @NonNull Password password, @NonNull Name name, @NonNull Name surname, @NonNull DormitoryId dormitoryId, @NonNull RoomNumber roomNumber) {
        ResidentId id = authService.registerResident(email, password);

        ResidentDto dto = new ResidentDto(id, name, surname, dormitoryId, roomNumber);

        userProfileRepository.registerResident(dto);
    }

    @Override
    @Transactional
    public void registerStaff(@NonNull Email email, @NonNull Password password, @NonNull Name name, @NonNull Name surname, @NonNull DormitoryId dormitoryId) {
        StaffId id = authService.registerStaff(email, password);

        StaffDto dto = new StaffDto(id, name, surname, dormitoryId);

        userProfileRepository.registerStaff(dto);
    }
}

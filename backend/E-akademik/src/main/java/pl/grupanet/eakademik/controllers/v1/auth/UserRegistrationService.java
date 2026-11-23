package pl.grupanet.eakademik.controllers.v1.auth;

import org.commons.*;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.authentication.AuthService;
import pl.grupanet.eakademik.user.UserProfileRepository;
import pl.grupanet.eakademik.user.dto.ResidentDto;
import pl.grupanet.eakademik.user.dto.StaffDto;

@Service
class UserRegistrationService {

    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;

    public UserRegistrationService(UserProfileRepository userProfileRepository, AuthService authService) {
        this.userProfileRepository = userProfileRepository;
        this.authService = authService;
    }

    @Transactional
    public void registerResident(Email email, Password password, Name name, Name surname, DormitoryId dormitoryId, RoomNumber roomNumber) {
        ResidentId id = authService.registerResident(email, password);

        ResidentDto dto = new ResidentDto(id, name, surname, dormitoryId, roomNumber);

        userProfileRepository.registerResident(dto);
    }

    @Transactional
    public void registerStaff(Email email, Password password, Name name, Name surname, DormitoryId dormitoryId) {
        StaffId id = authService.registerStaff(email, password);

        StaffDto dto = new StaffDto(id, name, surname, dormitoryId);

        userProfileRepository.registerStaff(dto);
    }
}

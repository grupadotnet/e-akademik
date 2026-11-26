package pl.grupanet.eakademik.register;

import lombok.NonNull;
import org.commons.*;
import pl.grupanet.eakademik.authentication.Email;
import pl.grupanet.eakademik.authentication.Password;

public interface UserRegistrationService {
    void registerResident(@NonNull Email email, @NonNull Password password, @NonNull Name name, @NonNull Name surname, @NonNull DormitoryId dormitoryId, @NonNull RoomNumber roomNumber);

    void registerStaff(@NonNull Email email, @NonNull Password password, @NonNull Name name, @NonNull Name surname, @NonNull DormitoryId dormitoryId);
}

package pl.grupanet.eakademik.register;

import lombok.NonNull;
import org.commons.*;

public interface UserRegistrationRepository {
    void registerResident(@NonNull Email email, @NonNull Password password, @NonNull Name name, @NonNull Name surname, @NonNull DormitoryId dormitoryId, @NonNull RoomNumber roomNumber);

    void registerStaff(@NonNull Email email, @NonNull Password password, @NonNull Name name, @NonNull Name surname, @NonNull DormitoryId dormitoryId);
}

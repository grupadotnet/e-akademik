package pl.grupanet.eakademik.authentication;

import org.commons.users.ResidentId;
import org.commons.users.StaffId;

public interface AuthService {
    ResidentId registerResident(Email email, Password password);
    StaffId registerStaff(Email email, Password password);
    String loginUser(Email email, Password password);
}

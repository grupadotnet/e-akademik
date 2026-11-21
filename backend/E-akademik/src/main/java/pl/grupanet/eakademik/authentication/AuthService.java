package pl.grupanet.eakademik.authentication;

import org.commons.Email;
import org.commons.Password;

public interface AuthService {
    void registerResident(Email email, Password password);
    void registerStaff(Email email, Password password);
    String loginUser(Email email, Password password);
}

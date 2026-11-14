package pl.grupanet.eakademik.authentication;

import org.commons.Email;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(Email email) {
        super("User with email: "+email+" already exists");
    }
}

package pl.grupanet.eakademik.authentication;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(Email email) {
        super("User with email: "+email+" already exists");
    }
}

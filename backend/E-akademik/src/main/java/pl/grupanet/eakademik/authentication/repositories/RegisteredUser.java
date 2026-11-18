package pl.grupanet.eakademik.authentication.repositories;

import lombok.NonNull;
import org.commons.*;

public record RegisteredUser(UserId id, Email email, String passwordHash, Role role) {
    public RegisteredUser(
            @NonNull final UserId id,
            @NonNull final Email email,
            @NonNull final String passwordHash,
            @NonNull final Role role
    ) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}

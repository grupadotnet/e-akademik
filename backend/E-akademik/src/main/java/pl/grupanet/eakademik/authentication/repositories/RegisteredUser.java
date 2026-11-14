package pl.grupanet.eakademik.authentication.repositories;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.commons.*;

public record RegisteredUser(UserId id, Email email, String passwordHash, Name fullName, DormitoryId dormitoryId, RoomNumber roomNumber, Role role) {
    public RegisteredUser(
            @NonNull final UserId id,
            @NonNull final Email email,
            @NonNull final String passwordHash,
            @NonNull final Name fullName,
            @NonNull final DormitoryId dormitoryId,
            @Nullable final RoomNumber roomNumber,
            @NonNull final Role role
    ) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.dormitoryId = dormitoryId;
        this.roomNumber = roomNumber;
        this.role = role;
    }
}

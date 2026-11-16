package org.commons;

import lombok.NonNull;

import java.util.Objects;
import java.util.UUID;

public class UserId {
    private final UUID id;

    public UserId(@NonNull final UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(id, userId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}

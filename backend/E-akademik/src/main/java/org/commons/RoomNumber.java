package org.commons;


import lombok.Getter;
import lombok.NonNull;

import java.util.Locale;
import java.util.Objects;

@Getter
public class RoomNumber {
    private final String roomNumber;

    public RoomNumber(@NonNull final String roomNumber) {
        String normalizedRoomNumber = roomNumber.trim().toUpperCase(Locale.ROOT);
        if(normalizedRoomNumber.isBlank()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        this.roomNumber = normalizedRoomNumber;
    }

    @Override
    public String toString() {
        return this.roomNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomNumber that = (RoomNumber) o;
        return Objects.equals(roomNumber, that.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(roomNumber);
    }
}

package org.commons;


import lombok.NonNull;

import java.util.Locale;

public class RoomNumber extends Name {

    public RoomNumber(@NonNull final String roomNumber) {
        super(roomNumber.trim().toUpperCase(Locale.ROOT));
    }
}

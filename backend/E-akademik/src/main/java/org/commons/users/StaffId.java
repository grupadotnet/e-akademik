package org.commons.users;

import lombok.NonNull;

import java.util.UUID;

public class StaffId extends UserId{
    public StaffId(@NonNull UUID id) {
        super(id);
    }
}

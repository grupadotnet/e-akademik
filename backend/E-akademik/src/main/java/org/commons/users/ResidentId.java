package org.commons.users;

import lombok.NonNull;

import java.util.UUID;

public class ResidentId extends UserId{
    public ResidentId(@NonNull UUID id) {
        super(id);
    }
}

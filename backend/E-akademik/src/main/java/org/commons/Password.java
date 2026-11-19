package org.commons;

import lombok.NonNull;

public class Password extends Name{

    public Password(@NonNull String password) {
        super(validatePassword(password));
    }

    private static String validatePassword(@NonNull String password) {
        if(password.length() < 8) {
            throw new IllegalArgumentException("Password length should be at least 8 characters");
        }

        return password;
    }
}
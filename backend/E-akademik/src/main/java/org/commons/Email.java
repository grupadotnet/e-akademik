package org.commons;

import lombok.NonNull;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private final String email;
    private static final Pattern email_pattern = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email(@NonNull String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if(!email_pattern.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Invalid email address");
        }
        this.email = normalizedEmail;
    }

    @Override
    public String toString() {
        return this.email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email1 = (Email) o;
        return Objects.equals(email, email1.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }
}

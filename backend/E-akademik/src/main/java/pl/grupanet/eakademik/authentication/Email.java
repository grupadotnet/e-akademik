package pl.grupanet.eakademik.authentication;

import lombok.NonNull;
import org.commons.Name;

import java.util.Locale;
import java.util.regex.Pattern;

public class Email extends Name {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email(@NonNull String email) {
        super(validateEmail(email));
    }

    private static String validateEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if(!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Invalid email address");
        }
        return normalizedEmail;
    }
}

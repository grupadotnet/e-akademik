package pl.grupanet.eakademik.authentication.security;

import org.commons.Email;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;

import java.util.List;

class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Email validEmail;
        try {
            validEmail = new Email(email);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new UsernameNotFoundException("Invalid email", e);
        }

        return userRepository.findByEmail(validEmail)
            .map(user -> new User(
                    user.email().toString(),
                    user.passwordHash(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name()))
            ))
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

package pl.grupanet.eakademik.authentication;

import org.commons.Role;
import org.commons.users.ResidentId;
import org.commons.users.StaffId;
import org.commons.users.UserId;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.authentication.jwt.TokenService;
import pl.grupanet.eakademik.authentication.repositories.RegisteredUser;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;

class AppAuthService implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AppAuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Transactional
    @Override
    public ResidentId registerResident(Email email, Password password) {
        UserId id = registerUser(email, password, Role.RESIDENT);
        return new ResidentId(id.getIdAsUUID());

    }

    @Transactional
    @Override
    public StaffId registerStaff(Email email, Password password) {
        UserId id = registerUser(email, password, Role.STAFF);
        return new StaffId(id.getIdAsUUID());
    }

    private UserId registerUser(Email email, Password password, Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        RegisteredUser user = new RegisteredUser(
                UserId.generate(),
                email,
                passwordEncoder.encode(password.toString()),
                role
        );

        RegisteredUser createdUser = userRepository.save(user);
        return createdUser.id();
    }

    public String loginUser(Email email, Password password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email.toString(), password.toString())
        );

        RegisteredUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        return tokenService.generateToken(user.id(), user.email(), user.role());
    }

}

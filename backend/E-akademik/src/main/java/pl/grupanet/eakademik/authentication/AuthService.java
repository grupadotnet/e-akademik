package pl.grupanet.eakademik.authentication;

import org.commons.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.grupanet.eakademik.authentication.jwt.TokenService;
import pl.grupanet.eakademik.authentication.repositories.RegisteredUser;
import pl.grupanet.eakademik.authentication.repositories.UserRepository;
import pl.grupanet.eakademik.dormitory.repositories.DormitoryRepository;

@Service
class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final DormitoryRepository dormitoryRepository;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, DormitoryRepository dormitoryRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.dormitoryRepository = dormitoryRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    void registerResident(RegisterResidentRequest registerResidentRequest) {
        registerUser(
                registerResidentRequest.universityName(),
                registerResidentRequest.dormitoryName(),
                new RoomNumber(registerResidentRequest.roomNumber()),
                registerResidentRequest.fullName(),
                registerResidentRequest.email(),
                registerResidentRequest.password(),
                Role.RESIDENT
        );
    }

    @Transactional
    void registerStaff(RegisterStaffRequest registerStaffRequest) {
        registerUser(
                registerStaffRequest.universityName(),
                registerStaffRequest.dormitoryName(),
                null,
                registerStaffRequest.fullName(),
                registerStaffRequest.email(),
                registerStaffRequest.password(),
                Role.STAFF
        );
    }

    private void registerUser(
            String universityName,
            String dormitoryName,
            RoomNumber roomNumber,
            String fullName,
            String email,
            String password,
            Role role
    ) {
        Email validEmail = new Email(email);
        Name validName = new Name(fullName);
        DormitoryId dormitoryId = new DormitoryId(new Name(universityName), new Name(dormitoryName));

        if(password == null || password.isBlank() || password.length() < 8) {
            throw new IllegalArgumentException("Password length should be at least 8 characters");
        }

        dormitoryRepository.get(dormitoryId);

        if(userRepository.existsByEmail(validEmail)) {
            throw new UserAlreadyExistsException(validEmail);
        }

        RegisteredUser user = new RegisteredUser(
                UserId.generate(),
                validEmail,
                passwordEncoder.encode(password),
                validName,
                dormitoryId,
                roomNumber,
                role
        );

        userRepository.save(user);
    }

    String loginUser(LoginRequest request) {
        Email validEmail = new Email(request.email());

        if (request.password() == null || request.password().isBlank()) {
            throw new BadCredentialsException("Invalid credentials");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(validEmail.toString(), request.password())
        );

        RegisteredUser user = userRepository.findByEmail(validEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        return tokenService.generateToken(user.id(), user.email(), user.role());
    }

}

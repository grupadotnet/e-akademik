package pl.grupanet.eakademik.seed;

import org.commons.*;

import org.springframework.boot.CommandLineRunner;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;
import pl.grupanet.eakademik.register.UserRegistrationService;

public class StartupUsersSeeder implements CommandLineRunner {
    private final UserRegistrationService userRegistrationService;

    public StartupUsersSeeder(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @Override
    public void run(String... args) throws Exception {
        DormitoryId bartek = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Ds-3 Bartek"));

        userRegistrationService.registerStaff(
                new Email("staff@e-akademik.pl"),
                new Password("staff12345"),
                new Name("Jan"),
                new Name("Kowalski"),
                bartek);

        userRegistrationService.registerResident(
                new Email("resident@e-akademik.pl"),
                new Password("resident123"),
                new Name("Anna"),
                new Name("Nowak"),
                bartek,
                new RoomNumber("101")
        );
    }
}

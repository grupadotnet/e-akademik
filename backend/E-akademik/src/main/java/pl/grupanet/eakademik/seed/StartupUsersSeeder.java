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

        //staff
        userRegistrationService.registerStaff(
                new Email("staff1@e-akademik.pl"),
                new Password("staff12345"),
                new Name("Jan"),
                new Name("Kowalski"),
                bartek);

        userRegistrationService.registerStaff(
                new Email("staff2@e-akademik.pl"),
                new Password("staff12345"),
                new Name("Marek"),
                new Name("Lewandowski"),
                bartek);

        userRegistrationService.registerStaff(
                new Email("staff3@e-akademik.pl"),
                new Password("staff12345"),
                new Name("Barbara"),
                new Name("Wójcik"),
                bartek);

        //resident
        userRegistrationService.registerResident(
                new Email("resident1@e-akademik.pl"),
                new Password("resident123"),
                new Name("Anna"),
                new Name("Nowak"),
                bartek,
                new RoomNumber("101")
        );

        userRegistrationService.registerResident(
                new Email("resident2@e-akademik.pl"),
                new Password("resident123"),
                new Name("Tomasz"),
                new Name("Zieliński"),
                bartek,
                new RoomNumber("201")
        );

        userRegistrationService.registerResident(
                new Email("resident3@e-akademik.pl"),
                new Password("resident123"),
                new Name("Julia"),
                new Name("Kamińska"),
                bartek,
                new RoomNumber("301")
        );
    }
}

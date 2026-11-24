package pl.grupanet.eakademik.register;

import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

public interface RegistrationRequest {
    UniversityNames universityName();
    String dormitoryName();
    String name();
    String surname();
    String email();
    String password();
}

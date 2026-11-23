package pl.grupanet.eakademik.controllers.v1.auth;

import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

interface RegistrationRequest {
    UniversityNames universityName();
    String dormitoryName();
    String name();
    String surname();
    String email();
    String password();
}

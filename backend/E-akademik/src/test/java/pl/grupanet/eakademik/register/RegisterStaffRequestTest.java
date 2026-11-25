package pl.grupanet.eakademik.register;

import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterStaffRequestTest {

    @Test
    void shouldCreateRegisterStaffRequestAndExposeFields() {
        RegisterStaffRequest request = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "DS1",
                "Anna",
                "Nowak",
                "anna.nowak@staff.pk.edu.pl",
                "SecretPassword123!"
        );

        assertThat(request.universityName()).isEqualTo(UniversityNames.POLITECHNIKA_KRAKOWSKA);
        assertThat(request.dormitoryName()).isEqualTo("DS1");
        assertThat(request.name()).isEqualTo("Anna");
        assertThat(request.surname()).isEqualTo("Nowak");
        assertThat(request.email()).isEqualTo("anna.nowak@staff.pk.edu.pl");
        assertThat(request.password()).isEqualTo("SecretPassword123!");
    }

    @Test
    void shouldSupportNullFields() {
        RegisterStaffRequest request = new RegisterStaffRequest(null, null, null, null, null, null);

        assertThat(request.universityName()).isNull();
        assertThat(request.dormitoryName()).isNull();
        assertThat(request.name()).isNull();
        assertThat(request.surname()).isNull();
        assertThat(request.email()).isNull();
        assertThat(request.password()).isNull();
    }

    @Test
    void shouldSatisfyEqualsAndHashCodeContract() {
        RegisterStaffRequest base = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "Anna", "Nowak", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest identical = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "Anna", "Nowak", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );

        RegisterStaffRequest diffUniversity = new RegisterStaffRequest(
                UniversityNames.AGH, "DS1", "Anna", "Nowak", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest diffDormitory = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS2", "Anna", "Nowak", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest diffName = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "Jan", "Nowak", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest diffSurname = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "Anna", "Kowalska", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest diffEmail = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "Anna", "Nowak", "jan.kowalski@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest diffPassword = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "Anna", "Nowak", "anna.nowak@staff.pk.edu.pl", "OtherPass123!"
        );

        assertThat(base)
                .isEqualTo(base)
                .isEqualTo(identical)
                .hasSameHashCodeAs(identical)
                .isNotEqualTo(diffUniversity)
                .isNotEqualTo(diffDormitory)
                .isNotEqualTo(diffName)
                .isNotEqualTo(diffSurname)
                .isNotEqualTo(diffEmail)
                .isNotEqualTo(diffPassword)
                .isNotEqualTo(null)
                .isNotEqualTo("some string");
    }

    @Test
    void shouldIncludeFieldValuesInToString() {
        RegisterStaffRequest request = new RegisterStaffRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "DS1",
                "Anna",
                "Nowak",
                "anna.nowak@staff.pk.edu.pl",
                "SecretPassword123!"
        );

        assertThat(request.toString())
                .contains("RegisterStaffRequest")
                .contains("Politechnika Krakowska")
                .contains("DS1")
                .contains("Anna")
                .contains("Nowak")
                .contains("anna.nowak@staff.pk.edu.pl")
                .contains("SecretPassword123!");
    }
}

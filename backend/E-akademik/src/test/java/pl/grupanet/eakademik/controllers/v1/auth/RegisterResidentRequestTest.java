package pl.grupanet.eakademik.controllers.v1.auth;

import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterResidentRequestTest {

    @Test
    void shouldCreateRegisterResidentRequestAndExposeFields() {
        RegisterResidentRequest request = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "DS1",
                "101A",
                "Jan",
                "Kowalski",
                "jan.kowalski@student.pk.edu.pl",
                "SecretPassword123!"
        );

        assertThat(request.universityName()).isEqualTo(UniversityNames.POLITECHNIKA_KRAKOWSKA);
        assertThat(request.dormitoryName()).isEqualTo("DS1");
        assertThat(request.roomNumber()).isEqualTo("101A");
        assertThat(request.name()).isEqualTo("Jan");
        assertThat(request.surname()).isEqualTo("Kowalski");
        assertThat(request.email()).isEqualTo("jan.kowalski@student.pk.edu.pl");
        assertThat(request.password()).isEqualTo("SecretPassword123!");
    }

    @Test
    void shouldSupportNullFields() {
        RegisterResidentRequest request = new RegisterResidentRequest(null, null, null, null, null, null, null);

        assertThat(request.universityName()).isNull();
        assertThat(request.dormitoryName()).isNull();
        assertThat(request.roomNumber()).isNull();
        assertThat(request.name()).isNull();
        assertThat(request.surname()).isNull();
        assertThat(request.email()).isNull();
        assertThat(request.password()).isNull();
    }

    @Test
    void shouldSatisfyEqualsAndHashCodeContract() {
        RegisterResidentRequest base = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "101A", "Jan", "Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest identical = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "101A", "Jan", "Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );

        RegisterResidentRequest diffUniversity = new RegisterResidentRequest(
                UniversityNames.AGH, "DS1", "101A", "Jan", "Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffDormitory = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS2", "101A", "Jan", "Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffRoom = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "202B", "Jan", "Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffName = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "101A", "Anna", "Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffSurname = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "101A", "Jan", "Nowak", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffEmail = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "101A", "Jan", "Kowalski", "anna.nowak@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffPassword = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA, "DS1", "101A", "Jan", "Kowalski", "jan.kowalski@student.pk.edu.pl", "OtherPass123!"
        );

        assertThat(base)
                .isEqualTo(base)
                .isEqualTo(identical)
                .hasSameHashCodeAs(identical)
                .isNotEqualTo(diffUniversity)
                .isNotEqualTo(diffDormitory)
                .isNotEqualTo(diffRoom)
                .isNotEqualTo(diffName)
                .isNotEqualTo(diffSurname)
                .isNotEqualTo(diffEmail)
                .isNotEqualTo(diffPassword)
                .isNotEqualTo(null)
                .isNotEqualTo("some string");
    }

    @Test
    void shouldIncludeFieldValuesInToString() {
        RegisterResidentRequest request = new RegisterResidentRequest(
                UniversityNames.POLITECHNIKA_KRAKOWSKA,
                "DS1",
                "101A",
                "Jan",
                "Kowalski",
                "jan.kowalski@student.pk.edu.pl",
                "SecretPassword123!"
        );

        assertThat(request.toString())
                .contains("RegisterResidentRequest")
                .contains("Politechnika Krakowska")
                .contains("DS1")
                .contains("101A")
                .contains("Jan")
                .contains("Kowalski")
                .contains("jan.kowalski@student.pk.edu.pl")
                .contains("SecretPassword123!");
    }
}

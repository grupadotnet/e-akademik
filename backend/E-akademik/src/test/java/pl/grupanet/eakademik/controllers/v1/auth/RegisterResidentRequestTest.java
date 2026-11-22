package pl.grupanet.eakademik.controllers.v1.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterResidentRequestTest {

    @Test
    void shouldCreateRegisterResidentRequestAndExposeFields() {
        RegisterResidentRequest request = new RegisterResidentRequest(
                "Politechnika Krakowska",
                "DS1",
                "101A",
                "Jan Kowalski",
                "jan.kowalski@student.pk.edu.pl",
                "SecretPassword123!"
        );

        assertThat(request.universityName()).isEqualTo("Politechnika Krakowska");
        assertThat(request.dormitoryName()).isEqualTo("DS1");
        assertThat(request.roomNumber()).isEqualTo("101A");
        assertThat(request.fullName()).isEqualTo("Jan Kowalski");
        assertThat(request.email()).isEqualTo("jan.kowalski@student.pk.edu.pl");
        assertThat(request.password()).isEqualTo("SecretPassword123!");
    }

    @Test
    void shouldSupportNullFields() {
        RegisterResidentRequest request = new RegisterResidentRequest(null, null, null, null, null, null);

        assertThat(request.universityName()).isNull();
        assertThat(request.dormitoryName()).isNull();
        assertThat(request.roomNumber()).isNull();
        assertThat(request.fullName()).isNull();
        assertThat(request.email()).isNull();
        assertThat(request.password()).isNull();
    }

    @Test
    void shouldSatisfyEqualsAndHashCodeContract() {
        RegisterResidentRequest base = new RegisterResidentRequest(
                "Politechnika Krakowska", "DS1", "101A", "Jan Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest identical = new RegisterResidentRequest(
                "Politechnika Krakowska", "DS1", "101A", "Jan Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );

        RegisterResidentRequest diffUniversity = new RegisterResidentRequest(
                "AGH", "DS1", "101A", "Jan Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffDormitory = new RegisterResidentRequest(
                "Politechnika Krakowska", "DS2", "101A", "Jan Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffRoom = new RegisterResidentRequest(
                "Politechnika Krakowska", "DS1", "202B", "Jan Kowalski", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffFullName = new RegisterResidentRequest(
                "Politechnika Krakowska", "DS1", "101A", "Anna Nowak", "jan.kowalski@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffEmail = new RegisterResidentRequest(
                "Politechnika Krakowska", "DS1", "101A", "Jan Kowalski", "anna.nowak@student.pk.edu.pl", "SecretPassword123!"
        );
        RegisterResidentRequest diffPassword = new RegisterResidentRequest(
                "Politechnika Krakowska", "DS1", "101A", "Jan Kowalski", "jan.kowalski@student.pk.edu.pl", "OtherPass123!"
        );

        assertThat(base)
                .isEqualTo(base)
                .isEqualTo(identical)
                .hasSameHashCodeAs(identical)
                .isNotEqualTo(diffUniversity)
                .isNotEqualTo(diffDormitory)
                .isNotEqualTo(diffRoom)
                .isNotEqualTo(diffFullName)
                .isNotEqualTo(diffEmail)
                .isNotEqualTo(diffPassword)
                .isNotEqualTo(null)
                .isNotEqualTo("some string");
    }

    @Test
    void shouldIncludeFieldValuesInToString() {
        RegisterResidentRequest request = new RegisterResidentRequest(
                "Politechnika Krakowska",
                "DS1",
                "101A",
                "Jan Kowalski",
                "jan.kowalski@student.pk.edu.pl",
                "SecretPassword123!"
        );

        assertThat(request.toString())
                .contains("RegisterResidentRequest")
                .contains("Politechnika Krakowska")
                .contains("DS1")
                .contains("101A")
                .contains("Jan Kowalski")
                .contains("jan.kowalski@student.pk.edu.pl")
                .contains("SecretPassword123!");
    }
}
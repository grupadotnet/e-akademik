package pl.grupanet.eakademik.controllers.v1.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterStaffRequestTest {

    @Test
    void shouldCreateRegisterStaffRequestAndExposeFields() {
        RegisterStaffRequest request = new RegisterStaffRequest(
                "Politechnika Krakowska",
                "DS1",
                "Anna Nowak",
                "anna.nowak@staff.pk.edu.pl",
                "SecretPassword123!"
        );

        assertThat(request.universityName()).isEqualTo("Politechnika Krakowska");
        assertThat(request.dormitoryName()).isEqualTo("DS1");
        assertThat(request.fullName()).isEqualTo("Anna Nowak");
        assertThat(request.email()).isEqualTo("anna.nowak@staff.pk.edu.pl");
        assertThat(request.password()).isEqualTo("SecretPassword123!");
    }

    @Test
    void shouldSupportNullFields() {
        RegisterStaffRequest request = new RegisterStaffRequest(null, null, null, null, null);

        assertThat(request.universityName()).isNull();
        assertThat(request.dormitoryName()).isNull();
        assertThat(request.fullName()).isNull();
        assertThat(request.email()).isNull();
        assertThat(request.password()).isNull();
    }

    @Test
    void shouldSatisfyEqualsAndHashCodeContract() {
        RegisterStaffRequest base = new RegisterStaffRequest(
                "Politechnika Krakowska", "DS1", "Anna Nowak", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest identical = new RegisterStaffRequest(
                "Politechnika Krakowska", "DS1", "Anna Nowak", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );

        RegisterStaffRequest diffUniversity = new RegisterStaffRequest(
                "AGH", "DS1", "Anna Nowak", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest diffDormitory = new RegisterStaffRequest(
                "Politechnika Krakowska", "DS2", "Anna Nowak", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest diffFullName = new RegisterStaffRequest(
                "Politechnika Krakowska", "DS1", "Jan Kowalski", "anna.nowak@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest diffEmail = new RegisterStaffRequest(
                "Politechnika Krakowska", "DS1", "Anna Nowak", "jan.kowalski@staff.pk.edu.pl", "SecretPassword123!"
        );
        RegisterStaffRequest diffPassword = new RegisterStaffRequest(
                "Politechnika Krakowska", "DS1", "Anna Nowak", "anna.nowak@staff.pk.edu.pl", "OtherPass123!"
        );

        assertThat(base)
                .isEqualTo(base)
                .isEqualTo(identical)
                .hasSameHashCodeAs(identical)
                .isNotEqualTo(diffUniversity)
                .isNotEqualTo(diffDormitory)
                .isNotEqualTo(diffFullName)
                .isNotEqualTo(diffEmail)
                .isNotEqualTo(diffPassword)
                .isNotEqualTo(null)
                .isNotEqualTo("some string");
    }

    @Test
    void shouldIncludeFieldValuesInToString() {
        RegisterStaffRequest request = new RegisterStaffRequest(
                "Politechnika Krakowska",
                "DS1",
                "Anna Nowak",
                "anna.nowak@staff.pk.edu.pl",
                "SecretPassword123!"
        );

        assertThat(request.toString())
                .contains("RegisterStaffRequest")
                .contains("Politechnika Krakowska")
                .contains("DS1")
                .contains("Anna Nowak")
                .contains("anna.nowak@staff.pk.edu.pl")
                .contains("SecretPassword123!");
    }
}
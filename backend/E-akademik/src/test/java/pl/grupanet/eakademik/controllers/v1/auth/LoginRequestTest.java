package pl.grupanet.eakademik.controllers.v1.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void shouldCreateLoginRequestAndExposeFields() {
        LoginRequest request = new LoginRequest("john.doe@student.edu.pl", "SecretPassword123!");

        assertThat(request.email()).isEqualTo("john.doe@student.edu.pl");
        assertThat(request.password()).isEqualTo("SecretPassword123!");
    }

    @Test
    void shouldSupportNullFields() {
        LoginRequest request = new LoginRequest(null, null);

        assertThat(request.email()).isNull();
        assertThat(request.password()).isNull();
    }

    @Test
    void shouldSatisfyEqualsAndHashCodeContract() {
        LoginRequest request1 = new LoginRequest("john.doe@student.edu.pl", "SecretPassword123!");
        LoginRequest request2 = new LoginRequest("john.doe@student.edu.pl", "SecretPassword123!");
        LoginRequest differentEmail = new LoginRequest("jane.doe@student.edu.pl", "SecretPassword123!");
        LoginRequest differentPassword = new LoginRequest("john.doe@student.edu.pl", "OtherPassword123!");

        assertThat(request1)
                .isEqualTo(request1)
                .isEqualTo(request2)
                .hasSameHashCodeAs(request2)
                .isNotEqualTo(differentEmail)
                .isNotEqualTo(differentPassword)
                .isNotEqualTo(null)
                .isNotEqualTo("some string");
    }

    @Test
    void shouldIncludeFieldValuesInToString() {
        LoginRequest request = new LoginRequest("john.doe@student.edu.pl", "SecretPassword123!");

        assertThat(request.toString())
                .contains("LoginRequest")
                .contains("john.doe@student.edu.pl")
                .contains("SecretPassword123!");
    }
}
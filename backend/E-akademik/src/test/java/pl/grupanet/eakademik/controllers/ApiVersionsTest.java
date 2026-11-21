package pl.grupanet.eakademik.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiVersionsTest {

    @Test
    void shouldContainExpectedValuesInOrder() {
        assertThat(ApiVersions.values()).containsExactly(ApiVersions.V1);
    }

    @Test
    void shouldReturnLowercaseVersionInToString() {
        assertThat(ApiVersions.V1.toString()).isEqualTo("v1");
    }

    @ParameterizedTest
    @EnumSource(ApiVersions.class)
    void shouldReturnLowercaseEnumNameInToString(ApiVersions version) {
        assertThat(version.toString()).isEqualTo(version.name().toLowerCase());
    }

    @ParameterizedTest
    @EnumSource(ApiVersions.class)
    void shouldReturnFullApiPathForV1(ApiVersions version) {
        assertThat(version.getPath()).isEqualTo("/api/" + version + "/");
    }

    @Test
    void shouldResolveApiVersionFromString() {
        assertThat(ApiVersions.valueOf("V1")).isEqualTo(ApiVersions.V1);
    }

    @Test
    void shouldThrowExceptionForInvalidApiVersionName() {
        assertThatThrownBy(() -> ApiVersions.valueOf("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
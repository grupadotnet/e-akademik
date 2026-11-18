package org.commons;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.grupanet.eakademik.dormitory.spec.pk.UniversityNames;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DormitoryIdTest {

    @Test
    void shouldReturnCorrectNames() {
        UniversityNames universityName = UniversityNames.POLITECHNIKA_KRAKOWSKA;
        Name dormitoryName = new Name("Simmons");

        DormitoryId dormitoryId = new DormitoryId(universityName, dormitoryName);

        assertEquals(universityName, dormitoryId.getUniversityName());
        assertEquals(dormitoryName, dormitoryId.getDormitoryName());
    }

    @Test
    void shouldReturnCorrectStringRepresentation() {
        UniversityNames universityName = UniversityNames.POLITECHNIKA_KRAKOWSKA;
        Name dormitoryName = new Name("Simmons");
        DormitoryId dormitoryId = new DormitoryId(universityName, dormitoryName);

        String expectedValue = "Politechnika Krakowska-Simmons";

        assertEquals(expectedValue, dormitoryId.toString());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidArgumentsForConstructor")
    void shouldThrowExceptionWhenAnyNameIsNull(UniversityNames universityName, Name dormitoryName) {
        assertThrows(NullPointerException.class, () -> new DormitoryId(universityName, dormitoryName));
    }

    private static Stream<Arguments> provideInvalidArgumentsForConstructor() {
        return Stream.of(
                Arguments.of(null, new Name("Simmons")),
                Arguments.of(UniversityNames.POLITECHNIKA_KRAKOWSKA, null),
                Arguments.of(null, null)
        );
    }

    @Test
    void shouldBuildDormitoryIdUsingBuilder() {
        DormitoryId dormitoryId = new DormitoryId.Builder(UniversityNames.POLITECHNIKA_KRAKOWSKA, "Simmons").build();

        assertEquals(UniversityNames.POLITECHNIKA_KRAKOWSKA, dormitoryId.getUniversityName());
        assertEquals(new Name("Simmons"), dormitoryId.getDormitoryName());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidArgumentsForBuilder")
    void shouldThrowExceptionWhenBuilderInstantiatedWithNull(UniversityNames universityName, String dormitoryName) {
        assertThrows(NullPointerException.class, () -> new DormitoryId.Builder(universityName, dormitoryName));
    }

    private static Stream<Arguments> provideInvalidArgumentsForBuilder() {
        return Stream.of(
                Arguments.of(null, "Simmons"),
                Arguments.of(UniversityNames.POLITECHNIKA_KRAKOWSKA, null),
                Arguments.of(null, null)
        );
    }

    @Test
    void shouldBeEqualWhenValuesAreSame() {
        DormitoryId id1 = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Simmons"));
        DormitoryId id2 = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Simmons"));
        DormitoryId id3 = new DormitoryId.Builder(UniversityNames.POLITECHNIKA_KRAKOWSKA, "Simmons").build();

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
        assertEquals(id1, id3);
        assertEquals(id1.hashCode(), id3.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenUniversityNameIsDifferent() {
        DormitoryId id1 = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Simmons"));
        DormitoryId id2 = new DormitoryId(mock(UniversityNames.class), new Name("Simmons"));

        assertNotEquals(id1, id2);
    }

    @Test
    void shouldNotBeEqualWhenDormitoryNameIsDifferent() {
        DormitoryId id1 = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Simmons"));
        DormitoryId id2 = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Baker"));

        assertNotEquals(id1, id2);
    }

    @Test
    void shouldNotBeEqualToNullOrDifferentClass() {
        DormitoryId id = new DormitoryId(UniversityNames.POLITECHNIKA_KRAKOWSKA, new Name("Simmons"));

        assertNotEquals(null, id);
        assertNotEquals("MIT-Simmons", id);
    }
}
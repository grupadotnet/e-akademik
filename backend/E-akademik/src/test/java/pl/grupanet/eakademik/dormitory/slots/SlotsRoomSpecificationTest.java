package pl.grupanet.eakademik.dormitory.slots;

import org.commons.Name;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SlotsRoomSpecificationTest {

    @Test
    void shouldBuildSpecificationAndRetrieveCalculator() {
        ChooseSlotsCalculator calculatorMock = mock(ChooseSlotsCalculator.class);

        SlotsRoomSpecification spec = new SlotsRoomSpecification.Builder()
                .add("Pralnia Parter", calculatorMock)
                .build();

        ChooseSlotsCalculator retrieved = spec.getCalculatorByRoomName(new Name("Pralnia Parter"));

        assertNotNull(retrieved);
        assertEquals(calculatorMock, retrieved);
    }

    @Test
    void shouldBuildSpecificationWithMultipleRooms() {
        ChooseSlotsCalculator calc1 = mock(ChooseSlotsCalculator.class);
        ChooseSlotsCalculator calc2 = mock(ChooseSlotsCalculator.class);

        SlotsRoomSpecification spec = new SlotsRoomSpecification.Builder()
                .add("Pralnia 1", calc1)
                .add("Pralnia 2", calc2)
                .build();

        assertEquals(calc1, spec.getCalculatorByRoomName(new Name("Pralnia 1")));
        assertEquals(calc2, spec.getCalculatorByRoomName(new Name("Pralnia 2")));
    }

    @Test
    void shouldThrowExceptionWhenBuildingWithoutRooms() {
        SlotsRoomSpecification.Builder builder = new SlotsRoomSpecification.Builder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, builder::build);

        assertEquals("Rooms cannot be without slots", exception.getMessage());
    }

    @Test
    void shouldReturnNullWhenGettingCalculatorForNonExistentRoom() {
        ChooseSlotsCalculator calculatorMock = mock(ChooseSlotsCalculator.class);
        SlotsRoomSpecification spec = new SlotsRoomSpecification.Builder()
                .add("Pralnia Parter", calculatorMock)
                .build();

        ChooseSlotsCalculator retrieved = spec.getCalculatorByRoomName(new Name("Nieznana Pralnia"));

        assertNull(retrieved);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void shouldThrowNullPointerExceptionWhenGettingCalculatorWithNullName() {
        ChooseSlotsCalculator calculatorMock = mock(ChooseSlotsCalculator.class);
        SlotsRoomSpecification spec = new SlotsRoomSpecification.Builder()
                .add("Pralnia Parter", calculatorMock)
                .build();

        assertThrows(NullPointerException.class, () -> {
            spec.getCalculatorByRoomName(null);
        });
    }

    @Test
    void shouldHaveDefaultRoomSpecificationConfiguredCorrectly() {
        SlotsRoomSpecification defaultSpec = SlotsRoomSpecification.defaultRoomSpecification;

        assertNotNull(defaultSpec);

        ChooseSlotsCalculator retrievedCalculator = defaultSpec.getCalculatorByRoomName(new Name("room 1"));

        assertNotNull(retrievedCalculator);
        assertEquals(ChooseSlotsCalculator.defaultChooseCalculator, retrievedCalculator);
    }

    @Test
    void shouldBeEqualWhenInstancesHaveSameRoomsAndCalculators() {
        ChooseSlotsCalculator calc = mock(ChooseSlotsCalculator.class);

        SlotsRoomSpecification spec1 = new SlotsRoomSpecification.Builder()
                .add("211", calc)
                .add("411", calc)
                .build();
        SlotsRoomSpecification spec2 = new SlotsRoomSpecification.Builder()
                .add("211", calc)
                .add("411", calc)
                .build();

        assertEquals(spec1, spec2);
        assertEquals(spec1.hashCode(), spec2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenInstancesHaveDifferentRooms() {
        ChooseSlotsCalculator calc = mock(ChooseSlotsCalculator.class);

        SlotsRoomSpecification spec1 = new SlotsRoomSpecification.Builder()
                .add("211", calc)
                .build();
        SlotsRoomSpecification spec2 = new SlotsRoomSpecification.Builder()
                .add("211", calc)
                .add("411", calc)
                .build();

        assertNotEquals(spec1, spec2);
        assertNotEquals(null, spec1);
        assertNotEquals(spec1, new Object());
    }

}
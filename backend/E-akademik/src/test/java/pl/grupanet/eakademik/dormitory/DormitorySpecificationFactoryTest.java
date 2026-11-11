package pl.grupanet.eakademik.dormitory;

import org.commons.DormitoryId;
import org.commons.Name;
import org.commons.SlotNumber;
import org.junit.jupiter.api.Test;
import pl.grupanet.eakademik.dormitory.slots.ChooseSlotsCalculator;
import pl.grupanet.eakademik.dormitory.slots.SlotsCalculator;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DormitorySpecificationFactoryTest {

    @Test
    void shouldCreateDormitorySpecification() {
        DormitoryId dormitoryId = new DormitoryId(new Name("PK"), new Name("Dor1"));
        ChooseSlotsCalculator calculator = createChooseSlotsCalculator();
        DormitorySpecificationFactory factory = new DormitorySpecificationFactory(
                dormitoryId.getUniversityName().toString(),
                dormitoryId.getDormitoryName().toString()
        );

        factory.setChooseSlotsCalculator(calculator);
        DormitorySpecification specification = factory.create();

        assertEquals(dormitoryId, specification.dormitoryId());
        assertEquals(calculator, specification.chooseSlotsCalculator());
    }

    @Test
    void shouldThrowExceptionWhenDormitoryIdIsNull() {
        assertThrows(NullPointerException.class, () -> {
            new DormitorySpecificationFactory(null, null);
        });
        assertThrows(NullPointerException.class, () -> {
            new DormitorySpecificationFactory("PK", null);
        });
        assertThrows(NullPointerException.class, () -> {
            new DormitorySpecificationFactory(null, "Dor1");
        });
    }

    @Test
    void shouldCreateSpecificationWithNotNullCalculatorWhenNotSet() {
        DormitoryId dormitoryId = new DormitoryId(new Name("PK"), new Name("Dor1"));
        DormitorySpecificationFactory factory = new DormitorySpecificationFactory(
                dormitoryId.getUniversityName().toString(),
                dormitoryId.getDormitoryName().toString()
        );

        DormitorySpecification specification = factory.create();

        assertEquals(dormitoryId, specification.dormitoryId());
        assertNotNull(specification.chooseSlotsCalculator());
    }

    private static ChooseSlotsCalculator createChooseSlotsCalculator() {
        ChooseSlotsCalculator calculator = mock(ChooseSlotsCalculator.class);
        SlotsCalculator expectedSlotsCalculator = mock(SlotsCalculator.class);

        when(expectedSlotsCalculator.generateSlotIds()).thenReturn(List.of(new SlotNumber(0)));
        when(calculator.choose(any(LocalDateTime.class))).thenReturn(expectedSlotsCalculator);

        return calculator;
    }
}
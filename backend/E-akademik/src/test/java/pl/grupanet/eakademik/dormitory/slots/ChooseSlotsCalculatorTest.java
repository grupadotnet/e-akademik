package pl.grupanet.eakademik.dormitory.slots;

import org.commons.SlotNumber;
import org.commons.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChooseSlotsCalculatorTest {

    @Test
    void shouldReturnAssignedCalculatorForSpecificDay() {
        SlotsCalculator mondayCalculator = createSlotsCalculator();
        SlotsCalculator wednesdayCalculator = createSlotsCalculator();

        ChooseSlotsCalculator chooser = new ChooseSlotsCalculator.Builder()
                .add(DayOfWeek.MONDAY, mondayCalculator)
                .add(DayOfWeek.WEDNESDAY, wednesdayCalculator)
                .build();

        LocalDateTime mondayDate = LocalDateTime.of(2026, 11, 9, 12, 0);

        SlotsCalculator chosenCalculator = chooser.choose(mondayDate);

        assertEquals(mondayCalculator, chosenCalculator);
    }

    @Test
    void shouldReturnDefaultCalculatorWhenDayIsNotConfigured() {
        SlotsCalculator defaultCalc = createSlotsCalculator();
        SlotsCalculator mondayCalc = createSlotsCalculator();

        ChooseSlotsCalculator chooser = new ChooseSlotsCalculator.Builder(defaultCalc)
                .add(DayOfWeek.MONDAY, mondayCalc)
                .build();

        LocalDateTime wednesdayDate = LocalDateTime.of(2026, 11, 11, 12, 0);

        SlotsCalculator chosenCalculator = chooser.choose(wednesdayDate);

        assertEquals(defaultCalc, chosenCalculator);
    }

    @Test
    void shouldReturnDefaultCalculatorCreatedByEmptyBuilder() {
        ChooseSlotsCalculator chooser = new ChooseSlotsCalculator.Builder(createSlotsCalculator()).build();

        LocalDateTime randomDate = LocalDateTime.of(2026, 11, 12, 12, 0);

        SlotsCalculator chosenCalculator = chooser.choose(randomDate);

        assertNotNull(chosenCalculator);
    }

    @Test
    void shouldThrowExceptionWhenNullDateIsProvided() {
        ChooseSlotsCalculator chooser = new ChooseSlotsCalculator.Builder(createSlotsCalculator()).build();

        assertThrows(NullPointerException.class, () -> {
            chooser.choose(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenAddingNullDayToBuilder() {
        ChooseSlotsCalculator.Builder builder = new ChooseSlotsCalculator.Builder();
        SlotsCalculator dummyCalc = new SlotsCalculator();

        assertThrows(NullPointerException.class, () -> {
            builder.add(null, dummyCalc);
        });
    }

    @Test
    void shouldThrowExceptionWhenAddingNullCalculatorToBuilder() {
        ChooseSlotsCalculator.Builder builder = new ChooseSlotsCalculator.Builder();

        assertThrows(NullPointerException.class, () -> {
            builder.add(DayOfWeek.MONDAY, null);
        });
    }

    @Test
    void shouldThrowExceptionWhenCreatingBuilderWithNullDefaultCalculator() {
        assertThrows(NullPointerException.class, () -> {
            new ChooseSlotsCalculator.Builder(null);
        });
    }

    @Test
    void shouldThrowExceptionWhenAnyCalculatorGeneratesSlots() {
        SlotsCalculator emptyCalculator = new SlotsCalculator() {
            @Override
            public List<SlotNumber> generateSlotIds() {
                return Collections.emptyList();
            }
        };


        ChooseSlotsCalculator.Builder builder = new ChooseSlotsCalculator.Builder(emptyCalculator);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    void shouldBuildSuccessfullyWhenAllCalculatorsGenerateEmptySlots() {
        SlotsCalculator nonEmptyCalculator = new SlotsCalculator() {
            @Override
            public List<SlotNumber> generateSlotIds() {
                return List.of(new SlotNumber(1));
            }
        };

        ChooseSlotsCalculator.Builder builder = new ChooseSlotsCalculator.Builder(nonEmptyCalculator);

        assertDoesNotThrow(builder::build);
    }

    private static SlotsCalculator createSlotsCalculator() {
        return new SlotsCalculator(new TimeSlot(LocalTime.of(1, 0),
                LocalTime.of(20, 0)),
                Duration.ofMinutes(10),
                Duration.ofMinutes(1));
    }
}
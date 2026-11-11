package pl.grupanet.eakademik.dormitory.slots;

import lombok.NonNull;
import org.commons.TimeSlot;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

public class ChooseSlotsCalculator {
    public static ChooseSlotsCalculator defaultChooseCalculator = new Builder(
            new SlotsCalculator(
                    new TimeSlot(LocalTime.of(8,0),LocalTime.of(16,0)),
                    Duration.ofMinutes(45),
                    Duration.ofMinutes(15)
            )).build();

    public static class Builder {
        private final SlotsCalculator defaultCalculator;
        private final HashMap<DayOfWeek, SlotsCalculator> slotsCalculators = new HashMap<>();

        public Builder() {
            this(new SlotsCalculator());
        }

        public Builder(@NonNull final SlotsCalculator defaultCalculator) {
            this.defaultCalculator = defaultCalculator;
        }

        public Builder add(@NonNull final DayOfWeek day, @NonNull final SlotsCalculator calculator) {
            slotsCalculators.put(day, calculator);
            return this;
        }

        public ChooseSlotsCalculator build() {
            return new ChooseSlotsCalculator(defaultCalculator, slotsCalculators);
        }

    }

    private final SlotsCalculator defaultCalculator;
    private final HashMap<DayOfWeek, SlotsCalculator> slotsCalculators;

    public ChooseSlotsCalculator(SlotsCalculator defaultCalculator, HashMap<DayOfWeek, SlotsCalculator> slotsCalculators) {
        if (!isValid(defaultCalculator, slotsCalculators))
            throw new IllegalArgumentException("Cannot create week slots without any slot");

        this.defaultCalculator = defaultCalculator;
        this.slotsCalculators = slotsCalculators;

    }

    public SlotsCalculator choose(@NonNull final LocalDateTime localDateTime) {
        return choose(localDateTime.getDayOfWeek());
    }

    private SlotsCalculator choose(@NonNull DayOfWeek dayOfWeek) {
        return slotsCalculators.getOrDefault(dayOfWeek, defaultCalculator);
    }

    private boolean isValid(SlotsCalculator defaultCalculator, HashMap<DayOfWeek, SlotsCalculator> slotsCalculators) {
        for (DayOfWeek value : DayOfWeek.values()) {
            if (!slotsCalculators.getOrDefault(value, defaultCalculator).generateSlotIds().isEmpty()) return true;
        }
        return false;
    }
}

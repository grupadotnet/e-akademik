package pl.grupanet.eakademik.dormitory.slots;

import lombok.NonNull;
import org.commons.SlotNumber;
import org.commons.TimeSlot;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SlotsCalculator {
    private final TimeSlot timeSlot;
    private final Duration slotDuration;
    private final Duration breakDuration;

    public SlotsCalculator() {
        this(new TimeSlot(LocalTime.of(1, 0), LocalTime.of(1, 1))
                , Duration.ofMinutes(10),
                Duration.ofMinutes(10));
    }

    public SlotsCalculator(@NonNull final TimeSlot timeSlot,
                           @NonNull final Duration slotDuration,
                           @NonNull final Duration breakDuration) {
        this.timeSlot = timeSlot;
        this.slotDuration = slotDuration;
        this.breakDuration = breakDuration;
    }

    public List<SlotNumber> generateSlotIds() {
        List<SlotNumber> slotIds = new ArrayList<>();

        long totalDurationMinutes = Duration.between(timeSlot.startTime(), timeSlot.endTime()).toMinutes();
        long slotTotalMinutes = slotDuration.toMinutes() + breakDuration.toMinutes();

        if (totalDurationMinutes <= 0) {
            return slotIds;
        }

        int slotIndex = 0;
        long currentMinutesFromStart = 0;

        while (currentMinutesFromStart + slotDuration.toMinutes() <= totalDurationMinutes) {
            slotIds.add(new SlotNumber(slotIndex));
            currentMinutesFromStart += slotTotalMinutes;
            slotIndex++;
        }

        return slotIds;
    }

    public TimeSlot calculateSlotHoursById(@NonNull SlotNumber slotNumber) {
        if (generateSlotIds().isEmpty()) return null;

        int index = slotNumber.getNumberAsInt();

        long totalMinutesPerStep = slotDuration.toMinutes() + breakDuration.toMinutes();
        long offsetMinutes = index * totalMinutesPerStep;

        LocalTime calculatedStart = timeSlot.startTime().plusMinutes(offsetMinutes);
        LocalTime calculatedEnd = calculatedStart.plus(slotDuration);

        return new TimeSlot(calculatedStart, calculatedEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotsCalculator that = (SlotsCalculator) o;
        return Objects.equals(timeSlot, that.timeSlot) && Objects.equals(slotDuration, that.slotDuration) && Objects.equals(breakDuration, that.breakDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeSlot, slotDuration, breakDuration);
    }
}
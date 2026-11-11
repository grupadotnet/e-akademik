package org.commons;

import java.time.LocalTime;

public record TimeSlot(LocalTime startTime, LocalTime endTime)  {
    public TimeSlot {
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}

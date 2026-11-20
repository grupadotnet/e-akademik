package org.commons;

import java.util.Objects;

public class SlotNumber {
    private final int number;

    public SlotNumber(int number) {
        if (number < 0) throw new IllegalArgumentException("Number must be positive");

        this.number = number;
    }

    public int getNumberAsInt() {
        return number;
    }

    @Override
    public String toString() {
        return number+"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotNumber that = (SlotNumber) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}

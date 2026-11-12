package org.commons;

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
}

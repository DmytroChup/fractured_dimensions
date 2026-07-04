package tnpl.fractureddimensions.util;

import net.minecraft.world.inventory.ContainerData;

import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

/**
 * A helper class that cleanly synchronizes 64-bit longs and 32-bit ints
 * over Minecraft's 16-bit ContainerData network packets without cluttering block entities.
 */
public class LongDataHelper implements ContainerData {
    private final LongSupplier[] getters;
    private final LongConsumer[] setters;

    public LongDataHelper(LongSupplier[] getters, LongConsumer[] setters) {
        if (getters.length != setters.length) {
            throw new IllegalArgumentException("Getters and Setters arrays must have the same length");
        }
        this.getters = getters;
        this.setters = setters;
    }

    @Override
    public int get(int index) {
        int variableIndex = index / 2;
        boolean isHighBytes = (index % 2) != 0;
        long value = getters[variableIndex].getAsLong();

        if (isHighBytes) {
            return (int) ((value >> 16) & 0xFFFF);
        } else {
            return (int) (value & 0xFFFF);
        }
    }

    @Override
    public void set(int index, int value) {
        int variableIndex = index / 2;
        boolean isHighBytes = (index % 2) != 0;
        long currentValue = getters[variableIndex].getAsLong();

        if (isHighBytes) {
            long newValue = (currentValue & 0xFFFF) | (((long) value & 0xFFFF) << 16);
            setters[variableIndex].accept(newValue);
        } else {
            long newValue = (currentValue & 0xFFFF0000L) | (value & 0xFFFF);
            setters[variableIndex].accept(newValue);
        }
    }

    @Override
    public int getCount() {
        return getters.length * 2;
    }
}

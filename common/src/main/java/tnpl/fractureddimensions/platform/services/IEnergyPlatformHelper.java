package tnpl.fractureddimensions.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IEnergyPlatformHelper {

    /**
     * Checks if the block at the specified position can accept or extract energy.
     */
    boolean isEnergyContainer(Level level, BlockPos pos, @Nullable Direction side);

    /**
     * Returns the current amount of energy stored in the block.
     */
    long getEnergyStored(Level level, BlockPos pos, @Nullable Direction side);

    /**
     * Returns the maximum energy capacity of the block.
     */
    long getMaxEnergyStored(Level level, BlockPos pos, @Nullable Direction side);

    /**
     * Attempts to insert energy into the block.
     *
     * @param amount   The amount of energy to insert.
     * @param simulate If true, simulates the insertion (returns the result but does not modify the block's energy).
     * @return The amount of energy that was (or would be) successfully inserted.
     */
    long insertEnergy(Level level, BlockPos pos, @Nullable Direction side, long amount, boolean simulate);

    /**
     * Attempts to extract energy from the block.
     *
     * @param amount   The maximum amount of energy to extract.
     * @param simulate If true, simulates the extraction (returns the result but does not modify the block's energy).
     * @return The amount of energy that was (or would be) successfully extracted.
     */
    long extractEnergy(Level level, BlockPos pos, @Nullable Direction side, long amount, boolean simulate);

    /**
     * Returns the string representation of the energy unit for this platform.
     */
    String getEnergyUnit();
}
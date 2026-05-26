package tnpl.fractureddimensions.platform.services;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface IEnergyPlatformHelper {

    /**
     * Checks if the specified BlockEntity supports energy capability on the given side.
     */
    boolean hasEnergySupport(BlockEntity blockEntity, @Nullable Direction side);

    /**
     * Retrieves the current amount of energy stored within the BlockEntity.
     */
    int getEnergyStored(BlockEntity blockEntity, @Nullable Direction side);

    /**
     * Retrieves the maximum energy capacity of the BlockEntity.
     */
    int getMaxEnergyCapacity(BlockEntity blockEntity, @Nullable Direction side);

    /**
     * Inserts energy into the BlockEntity.
     *
     * @param amount   The amount of energy to insert.
     * @param simulate If true, the insertion is only simulated (no state change).
     * @return The actual amount of energy that was (or would be) accepted.
     */
    int insertEnergy(BlockEntity blockEntity, @Nullable Direction side, int amount, boolean simulate);

    /**
     * Extracts energy from the BlockEntity.
     *
     * @param amount   The maximum amount of energy to extract.
     * @param simulate If true, the extraction is only simulated (no state change).
     * @return The actual amount of energy that was (or would be) extracted.
     */
    int extractEnergy(BlockEntity blockEntity, @Nullable Direction side, int amount, boolean simulate);
}
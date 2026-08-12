package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Unique;
import tnpl.fractureddimensions.registry.ModBlockEntities;

import tnpl.fractureddimensions.energy.IEnergyContainer;

public class EnergyCoreBlockEntity extends BlockEntity implements IEnergyContainer {

    @Unique
    private static final String NBT_ENERGY = "Energy";

    @Unique
    private static final String NBT_MAX_ENERGY = "MaxEnergy";

    private static final long DEFAULT_MAX_ENERGY = 10_000_000L;

    private long energy;
    private long maxEnergy;

    public EnergyCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_CORE.get(), pos, state);
        this.energy = 0L;
        this.maxEnergy = DEFAULT_MAX_ENERGY;
    }

    // ---- Getters ----

    public long getEnergy() {
        return energy;
    }

    public long getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * Sets the current energy level, clamping to [0, maxEnergy].
     */
    public void setEnergy(long energy) {
        this.energy = Math.clamp(energy, 0L, this.maxEnergy);
        setChanged();
    }

    /**
     * Sets the maximum energy capacity. Must be at least 0.
     * If current energy exceeds the new max, it is clamped.
     */
    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = Math.max(0L, maxEnergy);
        this.energy = Math.min(this.energy, this.maxEnergy);
        setChanged();
    }

    /**
     * Adds energy, returns the amount actually added (respects max limit).
     */
    public long addEnergy(long amount) {
        if (amount <= 0L) return 0L;

        long space = this.maxEnergy - this.energy;
        long added = Math.min(amount, space);
        this.energy += added;

        return added;
    }

    /**
     * Removes energy, returns the amount actually removed (will not go below 0).
     */
    public long removeEnergy(long amount) {
        if (amount <= 0L) return 0L;

        long removed = Math.min(amount, this.energy);
        this.energy -= removed;

        return removed;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putLong(NBT_ENERGY, this.energy);
        output.putLong(NBT_MAX_ENERGY, this.maxEnergy);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.maxEnergy = input.getLongOr(NBT_MAX_ENERGY, DEFAULT_MAX_ENERGY);
        this.energy = Math.clamp(input.getLongOr(NBT_ENERGY, 0L), 0L, this.maxEnergy);
    }

    @Override
    public long getMaxInsert() {
        return 100_000L; // Example max insert for core
    }

    @Override
    public long getMaxExtract() {
        return 100_000L; // Example max extract for core
    }

    @Override
    public boolean canInsert() {
        return true;
    }

    @Override
    public boolean canExtract() {
        return true;
    }
}

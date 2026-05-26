package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Unique;
import tnpl.fractureddimensions.registry.ModBlockEntities;

public class EnergyCoreBlockEntity extends BlockEntity {

    @Unique
    private static final String NBT_ENERGY = "Energy";

    @Unique
    private static final String NBT_MAX_ENERGY = "MaxEnergy";

    private static final int DEFAULT_MAX_ENERGY = 10_000;

    private int energy;
    private int maxEnergy;

    public EnergyCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_CORE.get(), pos, state);
        this.energy = 0;
        this.maxEnergy = DEFAULT_MAX_ENERGY;
    }

    // ---- Getters ----

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * Sets the current energy level, clamping to [0, maxEnergy].
     */
    public void setEnergy(int energy) {
        this.energy = Math.clamp(energy, 0, this.maxEnergy);
        setChanged();
    }

    /**
     * Sets the maximum energy capacity. Must be at least 0.
     * If current energy exceeds the new max, it is clamped.
     */
    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = Math.max(0, maxEnergy);
        this.energy = Math.min(this.energy, this.maxEnergy);
        setChanged();
    }

    /**
     * Adds energy, returns the amount actually added (respects max limit).
     */
    public int addEnergy(int amount) {
        if (amount <= 0) return 0;

        int space = this.maxEnergy - this.energy;
        int added = Math.min(amount, space);
        this.energy += added;
        setChanged();
        return added;
    }

    /**
     * Removes energy, returns the amount actually removed (will not go below 0).
     */
    public int removeEnergy(int amount) {
        if (amount <= 0) return 0;

        int removed = Math.min(amount, this.energy);
        this.energy -= removed;
        setChanged();
        return removed;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(NBT_ENERGY, this.energy);
        output.putInt(NBT_MAX_ENERGY, this.maxEnergy);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);

        this.maxEnergy = input.getIntOr(NBT_MAX_ENERGY, DEFAULT_MAX_ENERGY);
        this.energy = Math.clamp(input.getIntOr(NBT_ENERGY, 0), 0, this.maxEnergy);
    }
}

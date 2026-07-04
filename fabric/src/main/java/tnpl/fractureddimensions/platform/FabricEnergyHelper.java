package tnpl.fractureddimensions.platform;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import tnpl.fractureddimensions.platform.services.IEnergyPlatformHelper;

public class FabricEnergyHelper implements IEnergyPlatformHelper {

    private EnergyStorage getEnergyStorage(Level level, BlockPos pos, @Nullable Direction side) {
        if (level == null) return null;
        return EnergyStorage.SIDED.find(level, pos, side);
    }

    @Override
    public boolean isEnergyContainer(Level level, BlockPos pos, @Nullable Direction side) {
        return getEnergyStorage(level, pos, side) != null;
    }

    @Override
    public long getEnergyStored(Level level, BlockPos pos, @Nullable Direction side) {
        EnergyStorage energy = getEnergyStorage(level, pos, side);
        return energy != null ? energy.getAmount() : 0L;
    }

    @Override
    public long getMaxEnergyStored(Level level, BlockPos pos, @Nullable Direction side) {
        EnergyStorage energy = getEnergyStorage(level, pos, side);
        return energy != null ? energy.getCapacity() : 0L;
    }

    @Override
    public long insertEnergy(Level level, BlockPos pos, @Nullable Direction side, long amount, boolean simulate) {
        EnergyStorage energy = getEnergyStorage(level, pos, side);
        if (energy == null) return 0L;

        try (Transaction tx = Transaction.openOuter()) {
            long inserted = energy.insert(amount, tx);

            if (!simulate) {
                tx.commit();
            }

            return inserted;
        }
    }

    @Override
    public long extractEnergy(Level level, BlockPos pos, @Nullable Direction side, long amount, boolean simulate) {
        EnergyStorage energy = getEnergyStorage(level, pos, side);
        if (energy == null) return 0L;

        try (Transaction tx = Transaction.openOuter()) {
            long extracted = energy.extract(amount, tx);

            if (!simulate) {
                tx.commit();
            }

            return extracted;
        }
    }

    @Override
    public String getEnergyUnit() {
        return "E";
    }
}

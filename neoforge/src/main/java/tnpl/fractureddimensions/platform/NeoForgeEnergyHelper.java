package tnpl.fractureddimensions.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;
import tnpl.fractureddimensions.platform.services.IEnergyPlatformHelper;

public class NeoForgeEnergyHelper implements IEnergyPlatformHelper {

    private EnergyHandler getEnergyHandler(Level level, BlockPos pos, @Nullable Direction side) {
        if (level == null) return null;
        return level.getCapability(Capabilities.Energy.BLOCK, pos, side);
    }

    @Override
    public boolean isEnergyContainer(Level level, BlockPos pos, @Nullable Direction side) {
        return getEnergyHandler(level, pos, side) != null;
    }

    @Override
    public long getEnergyStored(Level level, BlockPos pos, @Nullable Direction side) {
        EnergyHandler energy = getEnergyHandler(level, pos, side);
        return energy != null ? energy.getAmountAsLong() : 0L;
    }

    @Override
    public long getMaxEnergyStored(Level level, BlockPos pos, @Nullable Direction side) {
        EnergyHandler energy = getEnergyHandler(level, pos, side);
        return energy != null ? energy.getCapacityAsLong() : 0L;
    }

    @Override
    public long insertEnergy(Level level, BlockPos pos, @Nullable Direction side, long amount, boolean simulate) {
        EnergyHandler energy = getEnergyHandler(level, pos, side);
        if (energy == null) return 0L;

        int amountToInsert = (int) Math.min(amount, Integer.MAX_VALUE);

        try (Transaction tx = Transaction.openRoot()) {
            long inserted = energy.insert(amountToInsert, tx);

            if (!simulate) {
                tx.commit();
            }

            return inserted;
        }
    }

    @Override
    public long extractEnergy(Level level, BlockPos pos, @Nullable Direction side, long amount, boolean simulate) {
        EnergyHandler energy = getEnergyHandler(level, pos, side);
        if (energy == null) return 0L;

        int amountToInsert = (int) Math.min(amount, Integer.MAX_VALUE);

        try (Transaction tx = Transaction.openRoot()) {
            long extracted = energy.extract(amountToInsert, tx);

            if (!simulate) {
                tx.commit();
            }

            return extracted;
        }
    }
}

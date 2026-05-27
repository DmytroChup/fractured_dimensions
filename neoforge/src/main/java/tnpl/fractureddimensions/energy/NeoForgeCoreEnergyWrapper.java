package tnpl.fractureddimensions.energy;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.block.entity.EnergyCoreBlockEntity;

public class NeoForgeCoreEnergyWrapper extends SnapshotJournal<Long> implements EnergyHandler {

    private final EnergyCoreBlockEntity core;

    public NeoForgeCoreEnergyWrapper(EnergyCoreBlockEntity core) {
        this.core = core;
    }

    @Override
    protected Long createSnapshot() {
        return core.getEnergy();
    }

    @Override
    protected void revertToSnapshot(Long snapshot) {
        core.setEnergy(snapshot);
    }

    @Override
    public long getAmountAsLong() {
        return core.getEnergy();
    }

    @Override
    public long getCapacityAsLong() {
        return core.getMaxEnergy();
    }

    @Override
    protected void onRootCommit(Long originalState) {
        core.setChanged();
    }

    @Override
    public int insert(int maxAmount, @NonNull TransactionContext transaction) {
        long space = core.getMaxEnergy() - core.getEnergy();
        long toInsert = Math.min(maxAmount, space);

        if (toInsert > 0L) {
            this.updateSnapshots(transaction);
            core.addEnergy(toInsert);
        }
        return (int) toInsert;
    }

    @Override
    public int extract(int maxAmount, @NonNull TransactionContext transaction) {
        long available = core.getEnergy();
        long toExtract = Math.min(maxAmount, available);

        if (toExtract > 0L) {
            this.updateSnapshots(transaction);
            core.removeEnergy(toExtract);
        }
        return (int) toExtract;
    }
}

package tnpl.fractureddimensions.energy;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class NeoForgeGenericEnergyWrapper extends SnapshotJournal<Long> implements EnergyHandler {

    private final IEnergyContainer container;

    public NeoForgeGenericEnergyWrapper(IEnergyContainer container) {
        this.container = container;
    }

    @Override
    protected Long createSnapshot() {
        return container.getEnergy();
    }

    @Override
    protected void revertToSnapshot(Long snapshot) {
        container.setEnergy(snapshot);
    }

    @Override
    protected void onRootCommit(Long originalState) {
        container.setChanged();
    }

    @Override
    public long getAmountAsLong() {
        return container.getEnergy();
    }

    @Override
    public long getCapacityAsLong() {
        return container.getMaxEnergy();
    }

    @Override
    public int insert(int maxAmount, TransactionContext transaction) {
        if (!container.canInsert()) return 0;
        long space = container.getMaxEnergy() - container.getEnergy();
        long toInsert = Math.min(maxAmount, Math.min(space, container.getMaxInsert()));
        
        if (toInsert > 0L) {
            this.updateSnapshots(transaction);
            container.setEnergy(container.getEnergy() + toInsert);
        }
        return (int) toInsert;
    }

    @Override
    public int extract(int maxAmount, TransactionContext transaction) {
        if (!container.canExtract()) return 0;
        long available = container.getEnergy();
        long toExtract = Math.min(maxAmount, Math.min(available, container.getMaxExtract()));
        
        if (toExtract > 0L) {
            this.updateSnapshots(transaction);
            container.setEnergy(container.getEnergy() - toExtract);
        }
        return (int) toExtract;
    }
}

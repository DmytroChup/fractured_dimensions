package tnpl.fractureddimensions.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

public class FabricGenericEnergyWrapper extends SnapshotParticipant<Long> implements EnergyStorage {

    private final IEnergyContainer container;

    public FabricGenericEnergyWrapper(IEnergyContainer container) {
        this.container = container;
    }

    @Override
    protected Long createSnapshot() {
        return container.getEnergy();
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        container.setEnergy(snapshot);
    }

    @Override
    protected void onFinalCommit() {
        container.setChanged();
    }

    @Override
    public long getAmount() {
        return container.getEnergy();
    }

    @Override
    public long getCapacity() {
        return container.getMaxEnergy();
    }

    @Override
    public boolean supportsInsertion() {
        return container.canInsert();
    }

    @Override
    public boolean supportsExtraction() {
        return container.canExtract();
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        if (!container.canInsert()) return 0;
        long space = container.getMaxEnergy() - container.getEnergy();
        long toInsert = Math.min(maxAmount, Math.min(space, container.getMaxInsert()));
        
        if (toInsert > 0L) {
            this.updateSnapshots(transaction);
            container.setEnergy(container.getEnergy() + toInsert);
        }
        return toInsert;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        if (!container.canExtract()) return 0;
        long available = container.getEnergy();
        long toExtract = Math.min(maxAmount, Math.min(available, container.getMaxExtract()));
        
        if (toExtract > 0L) {
            this.updateSnapshots(transaction);
            container.setEnergy(container.getEnergy() - toExtract);
        }
        return toExtract;
    }
}

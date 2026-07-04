package tnpl.fractureddimensions.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;
import tnpl.fractureddimensions.block.entity.MeteoricGeneratorBlockEntity;

public class FabricGeneratorEnergyWrapper extends SnapshotParticipant<Long> implements EnergyStorage {

    private final MeteoricGeneratorBlockEntity generator;

    public FabricGeneratorEnergyWrapper(MeteoricGeneratorBlockEntity generator) {
        this.generator = generator;
    }

    @Override
    protected Long createSnapshot() {
        return generator.getEnergy();
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        generator.setEnergy(snapshot);
    }

    @Override
    protected void onFinalCommit() {
        generator.setChanged();
    }

    @Override
    public long getAmount() {
        return generator.getEnergy();
    }

    @Override
    public long getCapacity() {
        return generator.getMaxEnergy();
    }

    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        return 0L;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        long available = generator.getEnergy();
        long toExtract = Math.min(maxAmount, available);

        if (toExtract > 0L) {
            this.updateSnapshots(transaction);
            generator.removeEnergy(toExtract);
        }
        return toExtract;
    }
}

package tnpl.fractureddimensions.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jspecify.annotations.NonNull;
import team.reborn.energy.api.EnergyStorage;
import tnpl.fractureddimensions.block.entity.EnergyCoreBlockEntity;

public class FabricCoreEnergyWrapper extends SnapshotParticipant<Long> implements EnergyStorage {

    private final EnergyCoreBlockEntity core;

    public FabricCoreEnergyWrapper(EnergyCoreBlockEntity core) {
        this.core = core;
    }

    @Override
    protected @NonNull Long createSnapshot() {
        return core.getEnergy();
    }

    @Override
    protected void readSnapshot(@NonNull Long snapshot) {
        core.setEnergy(snapshot);
    }

    @Override
    protected void onFinalCommit() {
        core.setChanged();
    }

    @Override
    public long getAmount() {
        return core.getEnergy();
    }

    @Override
    public long getCapacity() {
        return core.getMaxEnergy();
    }

    @Override
    public boolean supportsInsertion() {
        return EnergyStorage.super.supportsInsertion();
    }

    @Override
    public boolean supportsExtraction() {
        return EnergyStorage.super.supportsExtraction();
    }


    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        long space = core.getMaxEnergy() - core.getEnergy();
        long toInsert = Math.min(maxAmount, space);

        if (toInsert > 0L) {
            this.updateSnapshots(transaction);
            core.addEnergy(toInsert);
        }
        return toInsert;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        long available = core.getEnergy();
        long toExtract = Math.min(maxAmount, available);

        if (toExtract > 0L) {
            this.updateSnapshots(transaction);
            core.removeEnergy(toExtract);
        }
        return toExtract;
    }
}

package tnpl.fractureddimensions.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import org.jspecify.annotations.NonNull;
import team.reborn.energy.api.EnergyStorage;
import tnpl.fractureddimensions.block.entity.EnergyCoreBlockEntity;
import tnpl.fractureddimensions.block.entity.EnergyPortBlockEntity;

import java.util.List;

public class FabricPortEnergyWrapper extends SnapshotParticipant<long[]> implements EnergyStorage {

    private final EnergyPortBlockEntity port;

    public FabricPortEnergyWrapper(EnergyPortBlockEntity port) {
        this.port = port;
    }

    @Override
    protected long @NonNull [] createSnapshot() {
        List<EnergyCoreBlockEntity> cores = port.getCores();
        long[] snapshot = new long[cores.size()];
        for (int i = 0; i < cores.size(); i++) {
            snapshot[i] = cores.get(i).getEnergy();
        }
        return snapshot;
    }

    @Override
    protected void readSnapshot(long[] snapshot) {
        List<EnergyCoreBlockEntity> cores = port.getCores();
        for (int i = 0; i < Math.min(cores.size(), snapshot.length); i++) {
            cores.get(i).setEnergy(snapshot[i]);
        }
    }

    @Override
    protected void onFinalCommit() {
        for (EnergyCoreBlockEntity core : port.getCores()) {
            core.setChanged();
        }
    }

    @Override
    public long getAmount() {
        return 0L; // The port itself doesn't store energy
    }

    @Override
    public long getCapacity() {
        return 50_000_000L; // Max theoretical capacity it can distribute
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        if (maxAmount <= 0) return 0;

        this.updateSnapshots(transaction);
        return port.distributeEnergyToCores(maxAmount);
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return 0; // Extraction from the port is blocked
    }
}

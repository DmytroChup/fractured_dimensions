package tnpl.fractureddimensions.energy;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.block.entity.EnergyCoreBlockEntity;
import tnpl.fractureddimensions.block.entity.EnergyPortBlockEntity;

import java.util.List;
import java.util.Objects;

public class NeoForgePortEnergyWrapper extends SnapshotJournal<long[]> implements EnergyHandler {

    private final EnergyPortBlockEntity port;

    public NeoForgePortEnergyWrapper(EnergyPortBlockEntity port) {
        this.port = port;
    }

    @Override
    protected long[] createSnapshot() {
        List<EnergyCoreBlockEntity> cores = port.getCores();
        long[] snapshot = new long[cores.size()];
        for (int i = 0; i < cores.size(); i++) {
            snapshot[i] = cores.get(i).getEnergy();
        }
        return snapshot;
    }

    @Override
    protected void revertToSnapshot(long[] snapshot) {
        List<EnergyCoreBlockEntity> cores = port.getCores();
        for (int i = 0; i < Math.min(cores.size(), Objects.requireNonNull(snapshot).length); i++) {
            cores.get(i).setEnergy(snapshot[i]);
        }
    }

    @Override
    protected void onRootCommit(long[] originalState) {
        for (EnergyCoreBlockEntity core : port.getCores()) {
            core.setChanged();
        }
    }

    @Override
    public long getAmountAsLong() {
        return 0L; // The port itself doesn't store energy
    }

    @Override
    public long getCapacityAsLong() {
        return 50_000_000L; // Max theoretical capacity it can distribute
    }

    @Override
    public int insert(int maxAmount, @NonNull TransactionContext transaction) {
        if (maxAmount <= 0) return 0;

        this.updateSnapshots(transaction);
        long accepted = port.distributeEnergyToCores(maxAmount);
        return (int) accepted;
    }

    @Override
    public int extract(int maxAmount, @NonNull TransactionContext transaction) {
        return 0; // Extraction from the port is blocked
    }
}

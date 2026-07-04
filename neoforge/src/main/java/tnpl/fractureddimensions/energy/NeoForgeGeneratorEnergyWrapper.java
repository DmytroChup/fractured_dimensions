package tnpl.fractureddimensions.energy;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import tnpl.fractureddimensions.block.entity.MeteoricGeneratorBlockEntity;

public class NeoForgeGeneratorEnergyWrapper extends SnapshotJournal<Long> implements EnergyHandler {

    private final MeteoricGeneratorBlockEntity generator;

    public NeoForgeGeneratorEnergyWrapper(MeteoricGeneratorBlockEntity generator) {
        this.generator = generator;
    }

    @Override
    protected Long createSnapshot() {
        return generator.getEnergy();
    }

    @Override
    protected void revertToSnapshot(Long snapshot) {
        generator.setEnergy(snapshot);
    }

    @Override
    public long getAmountAsLong() {
        return generator.getEnergy();
    }

    @Override
    public long getCapacityAsLong() {
        return generator.getMaxEnergy();
    }

    @Override
    protected void onRootCommit(Long originalState) {
        generator.setChanged();
    }

    @Override
    public int insert(int maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public int extract(int maxAmount, TransactionContext transaction) {
        long available = generator.getEnergy();
        long toExtract = Math.min(maxAmount, available);

        if (toExtract > 0L) {
            this.updateSnapshots(transaction);
            generator.removeEnergy(toExtract);
        }
        return (int) toExtract;
    }
}

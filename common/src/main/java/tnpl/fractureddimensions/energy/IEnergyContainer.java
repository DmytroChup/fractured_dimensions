package tnpl.fractureddimensions.energy;

public interface IEnergyContainer {
    long getEnergy();
    void setEnergy(long energy);
    long getMaxEnergy();

    long getMaxInsert();
    long getMaxExtract();

    boolean canInsert();
    boolean canExtract();

    void setChanged();
}

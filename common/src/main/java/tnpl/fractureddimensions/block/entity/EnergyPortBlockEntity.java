package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tnpl.fractureddimensions.registry.ModBlockEntities;

import java.util.ArrayList;
import java.util.List;

public class EnergyPortBlockEntity extends BlockEntity {

    public EnergyPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_PORT.get(), pos, state);
    }

    public List<EnergyCoreBlockEntity> getCores() {
        if (this.level == null) return List.of();

        BlockPos controllerPos = MultiblockValidator.findControllerFrom(this.level, this.worldPosition);
        if (controllerPos == null) return List.of();

        List<EnergyCoreBlockEntity> cores = new ArrayList<>();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos pos = controllerPos.offset(dx, 1, dz);
                if (this.level.getBlockEntity(pos) instanceof EnergyCoreBlockEntity core) {
                    cores.add(core);
                }
            }
        }
        return cores;
    }

    public long distributeEnergyToCores(long amount) {
        if (this.level == null || amount <= 0) return 0;

        long remaining = amount;
        for (EnergyCoreBlockEntity core : getCores()) {
            if (remaining > 0) {
                long added = core.addEnergy(remaining);
                remaining -= added;
            } else {
                break;
            }
        }

        return amount - remaining;
    }
}

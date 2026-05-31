package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tnpl.fractureddimensions.registry.ModBlockEntities;

import java.util.List;

public class EnergyPortBlockEntity extends BlockEntity {

    public EnergyPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_PORT.get(), pos, state);
    }

    public List<EnergyCoreBlockEntity> getCores() {
        if (this.level == null) return java.util.List.of();

        BlockPos controllerPos = MultiblockValidator.findControllerFrom(this.level, this.worldPosition);
        if (controllerPos == null) return java.util.List.of();

        BlockPos[] corePositions = new BlockPos[]{
            controllerPos.offset(2, 1, -1),
            controllerPos.offset(-2, 1, -1),
            controllerPos.offset(2, 1, 1),
            controllerPos.offset(-2, 1, 1),
            controllerPos.offset(0, 1, -2)
        };

        java.util.List<EnergyCoreBlockEntity> cores = new java.util.ArrayList<>();
        for (BlockPos pos : corePositions) {
            if (this.level.getBlockEntity(pos) instanceof EnergyCoreBlockEntity core) {
                cores.add(core);
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

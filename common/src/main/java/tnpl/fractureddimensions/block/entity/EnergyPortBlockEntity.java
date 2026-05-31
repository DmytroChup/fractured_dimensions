package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tnpl.fractureddimensions.registry.ModBlockEntities;

public class EnergyPortBlockEntity extends BlockEntity {

    public EnergyPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_PORT.get(), pos, state);
    }

    public long distributeEnergyToCores(long amount) {
        if (this.level == null || amount <= 0) return 0;

        BlockPos controllerPos = MultiblockValidator.findControllerFrom(this.level, this.worldPosition);
        if (controllerPos == null) return 0;

        BlockEntity be = this.level.getBlockEntity(controllerPos);
        if (!(be instanceof AnchorControllerBlockEntity controller)) return 0;

        BlockPos[] corePositions = new BlockPos[]{
            controllerPos.offset(2, 1, -1),
            controllerPos.offset(-2, 1, -1),
            controllerPos.offset(2, 1, 1),
            controllerPos.offset(-2, 1, 1),
            controllerPos.offset(0, 1, -2)
        };

        long remaining = amount;
        long totalEnergy = 0;

        for (BlockPos pos : corePositions) {
            if (this.level.getBlockEntity(pos) instanceof EnergyCoreBlockEntity core) {
                if (remaining > 0) {
                    long added = core.addEnergy(remaining);
                    remaining -= added;
                }
                totalEnergy += core.getEnergy();
            }
        }

        if (totalEnergy >= 50_000_000L && controller.getCurrentState() == MultiblockState.IDLE) {
            controller.setCurrentState(MultiblockState.READY);
        }

        return amount - remaining;
    }
}

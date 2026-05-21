package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tnpl.fractureddimensions.registry.ModBlockEntities;

public class EnergyCoreBlockEntity extends BlockEntity {
    public EnergyCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_CORE.get(), pos, state);
    }
}

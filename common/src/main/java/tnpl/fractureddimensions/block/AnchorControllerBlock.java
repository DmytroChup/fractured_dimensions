package tnpl.fractureddimensions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tnpl.fractureddimensions.block.entity.AnchorControllerBlockEntity;
import tnpl.fractureddimensions.registry.ModBlockEntities;

public class AnchorControllerBlock extends Block implements EntityBlock {
    public AnchorControllerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new AnchorControllerBlockEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }

        return type == ModBlockEntities.ANCHOR_CONTROLLER.get()
                ? (lvl, pos, st, be) -> AnchorControllerBlockEntity.serverTick(lvl, pos, st, (AnchorControllerBlockEntity) be)
                : null;
    }
}


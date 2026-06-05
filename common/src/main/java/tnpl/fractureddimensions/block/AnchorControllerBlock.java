package tnpl.fractureddimensions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tnpl.fractureddimensions.block.entity.AnchorControllerBlockEntity;
import tnpl.fractureddimensions.block.entity.MultiblockState;
import tnpl.fractureddimensions.registry.ModBlockEntities;

public class AnchorControllerBlock extends Block implements EntityBlock {

    public static final EnumProperty<MultiblockState> MULTIBLOCK_STATE =
            EnumProperty.create("multiblock_state", MultiblockState.class);

    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public AnchorControllerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(MULTIBLOCK_STATE, MultiblockState.INCOMPLETE)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(MULTIBLOCK_STATE, FACING);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                         @NonNull BlockPos pos, @NonNull Player player,
                                                         @NonNull BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        MultiblockState multiblockState = state.getValue(MULTIBLOCK_STATE);
        if (multiblockState != MultiblockState.READY && multiblockState != MultiblockState.ACTIVE) {
            return InteractionResult.PASS;
        }

        if (level.getBlockEntity(pos) instanceof AnchorControllerBlockEntity be) {
            player.openMenu(be);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
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

    @Override
    public @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.INVISIBLE;
    }
}

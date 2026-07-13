package tnpl.fractureddimensions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import tnpl.fractureddimensions.block.entity.MeteoricGeneratorBlockEntity;
import tnpl.fractureddimensions.block.entity.PressBlockEntity;
import tnpl.fractureddimensions.registry.ModBlockEntities;
import tnpl.fractureddimensions.registry.ModBlocks;

public class PressBlock extends Block implements EntityBlock {

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty PROCESSING = BooleanProperty.create("processing");

    public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 38.0, 16.0);

    public PressBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PROCESSING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PROCESSING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxY() - 2 && level.getBlockState(pos.above()).canBeReplaced(context) && level.getBlockState(pos.above(2)).canBeReplaced(context)) {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
        return null;
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            @Nullable Orientation orientation,
            boolean isMoving
    ) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
        if (!(level.getBlockState(pos.above(1)).getBlock() instanceof PressPartBlock)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        level.setBlock(pos.above(1), ModBlocks.PRESS_PART.get().defaultBlockState().setValue(PressPartBlock.PART, 1), 3);
        level.setBlock(pos.above(2), ModBlocks.PRESS_PART.get().defaultBlockState().setValue(PressPartBlock.PART, 2), 3);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos above1 = pos.above(1);
        BlockState state1 = level.getBlockState(above1);
        if (state1.getBlock() instanceof PressPartBlock) {
            level.setBlock(above1, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, above1, Block.getId(state1));
        }
        BlockPos above2 = pos.above(2);
        BlockState state2 = level.getBlockState(above2);
        if (state2.getBlock() instanceof PressPartBlock) {
            level.setBlock(above2, Blocks.AIR.defaultBlockState(), 35);
            level.levelEvent(player, 2001, above2, Block.getId(state2));
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PressBlockEntity press) {
                player.openMenu(press);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PressBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.PRESS.get() ?
                (lvl, pos, st, be) -> PressBlockEntity.serverTick(lvl, pos, st, (PressBlockEntity) be) : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}

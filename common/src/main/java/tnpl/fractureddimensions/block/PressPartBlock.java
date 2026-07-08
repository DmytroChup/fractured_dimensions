package tnpl.fractureddimensions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import tnpl.fractureddimensions.registry.ModBlocks;

public class PressPartBlock extends Block {

    public static final IntegerProperty PART = IntegerProperty.create("part", 1, 2);

    public PressPartBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int part = state.getValue(PART);
        return PressBlock.SHAPE.move(0, -part, 0);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, net.minecraft.world.level.redstone.Orientation orientation, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, orientation, isMoving);
        BlockPos mainPos = pos.below(state.getValue(PART));
        if (!(level.getBlockState(mainPos).getBlock() instanceof PressBlock)) {
            level.destroyBlock(pos, false);
        } else if (state.getValue(PART) == 1 && !(level.getBlockState(pos.above()).getBlock() instanceof PressPartBlock)) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(ModBlocks.PRESS.get());
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos mainPos = pos.below(state.getValue(PART));
        BlockState mainState = level.getBlockState(mainPos);
        if (mainState.getBlock() instanceof PressBlock) {
            level.destroyBlock(mainPos, !player.isCreative());
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        BlockPos mainPos = pos.below(state.getValue(PART));
        BlockState mainState = level.getBlockState(mainPos);
        if (mainState.getBlock() instanceof PressBlock pressBlock) {
            return pressBlock.useWithoutItem(mainState, level, mainPos, player, hitResult.withPosition(mainPos));
        }
        return InteractionResult.PASS;
    }
}

package tnpl.fractureddimensions.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import tnpl.fractureddimensions.block.entity.AnchorControllerBlockEntity;
import tnpl.fractureddimensions.block.entity.MultiblockValidator;
import tnpl.fractureddimensions.registry.ModBlocks;

public class SpatialWrenchItem extends Item {

    public SpatialWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);
        Block clickedBlock = clickedState.getBlock();

        // Check if the clicked block is part of the multiblock
        if (!isMultiblockComponent(clickedBlock)) {
            return InteractionResult.PASS;
        }

        // If the player clicked the controller directly, validate immediately
        if (clickedBlock == ModBlocks.ANCHOR_CONTROLLER.get()) {
            return validateAndReport(level, clickedPos, player);
        }

        // Otherwise, search for the controller from this block's position
        BlockPos controllerPos = MultiblockValidator.findControllerFrom(level, clickedPos);
        if (controllerPos != null) {
            return validateAndReport(level, controllerPos, player);
        }

        // No controller found nearby
        player.sendSystemMessage(Component.literal("§eNo controller found nearby"));
        return InteractionResult.SUCCESS;
    }

    private boolean isMultiblockComponent(Block block) {
        return block == ModBlocks.ANCHOR_CONTROLLER.get()
                || block == ModBlocks.SPATIAL_FRAME.get()
                || block == ModBlocks.ENERGY_CORE.get()
                || block == ModBlocks.OBSERVER_PLATFORM.get()
                || block == Blocks.GLASS;
    }

    private InteractionResult validateAndReport(Level level, BlockPos controllerPos, Player player) {
        if (level.getBlockEntity(controllerPos) instanceof AnchorControllerBlockEntity be) {
            MultiblockValidator.ValidationResult result = be.validateStructure();

            if (result.isValid()) {
                player.sendSystemMessage(Component.literal(
                        "§a✔ The structure is assembled! Status: " + be.getCurrentState().getSerializedName()
                ));
            } else {
                BlockPos errPos = result.errorPos();
                String label = result.missingLabel();

                if (errPos != null) {
                    player.sendSystemMessage(Component.literal(
                            String.format("§c✘ Structure incomplete! Missing: §e%s §cat §7[%d, %d, %d]",
                                    label, errPos.getX(), errPos.getY(), errPos.getZ())
                    ));
                } else {
                    player.sendSystemMessage(Component.literal("§c✘ The structure is incomplete!"));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}

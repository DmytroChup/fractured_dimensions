package tnpl.fractureddimensions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.block.entity.EnergyCoreBlockEntity;

import org.jspecify.annotations.Nullable;
import tnpl.fractureddimensions.platform.Services;

public class EnergyCoreBlock extends Block implements EntityBlock {
    public EnergyCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new EnergyCoreBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state,
                                               Level level,
                                               BlockPos pos,
                                               Player player,
                                               BlockHitResult hitResult) {
        if (!level.isClientSide()) {

            long inserted = Services.ENERGY.insertEnergy(level, pos, hitResult.getDirection(), 5_000_000L, false);

            if (inserted > 0) {
                player.sendSystemMessage(Component.literal("§a" + inserted + " energy has been successfully injected!"));
            } else {
                player.sendSystemMessage(Component.literal("§cThe core is charged to max!"));
            }
        }

        return InteractionResult.SUCCESS;
    }
}

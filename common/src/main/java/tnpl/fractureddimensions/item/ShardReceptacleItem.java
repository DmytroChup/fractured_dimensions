package tnpl.fractureddimensions.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.registry.ModDataComponents;
import tnpl.fractureddimensions.registry.ModDimensions;
import tnpl.fractureddimensions.worldgen.IslandManager;


import java.util.Set;

public class ShardReceptacleItem extends Item {

    public ShardReceptacleItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.has(ModDataComponents.DIMENSION_DATA.get())) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        DimensionData data = stack.get(ModDataComponents.DIMENSION_DATA.get());
        if (data == null) {
            return InteractionResult.PASS;
        }

        boolean success = teleportToIsland(serverPlayer, serverLevel, data);

        if (success && !player.isCreative()) {
            stack.shrink(1);
        }

        return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    public static boolean teleportToIsland(ServerPlayer serverPlayer, ServerLevel serverLevel, DimensionData data) {
        MinecraftServer server = serverLevel.getServer();
        ServerLevel voidLevel = server.getLevel(ModDimensions.VOID_LEVEL);

        if (voidLevel == null) {
            Constants.LOG.error("ShardReceptacleItem: Void dimension ({}) not found!", ModDimensions.VOID_LEVEL);
            return false;
        }

        IslandManager islandManager = IslandManager.get(voidLevel);

        BlockPos targetPos = islandManager.allocateNextPosition();
        Constants.LOG.info("ShardReceptacleItem: New island '{}' at {} ({}min)",
                data.name(), targetPos, data.survivalTime());

        islandManager.addIsland(
                targetPos, 
                data, 
                voidLevel.getGameTime(), 
                serverLevel.dimension().identifier().toString(), 
                serverPlayer.blockPosition()
        );

        serverPlayer.teleportTo(
                voidLevel,
                targetPos.getX() + 0.5,
                targetPos.getY() + 5.0,
                targetPos.getZ() + 0.5,
                Set.of(),
                serverPlayer.getYRot(),
                serverPlayer.getXRot(),
                true
        );

        serverPlayer.addEffect(
                new MobEffectInstance(
                        MobEffects.SLOW_FALLING,
                        200, // 10 seconds
                        0,
                        false,
                        false
                )
        );
        return true;
    }
}

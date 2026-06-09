package tnpl.fractureddimensions.item;

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
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.registry.ModDataComponents;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.registry.ModDimensions;

import java.util.Set;

public class ShardReceptacleItem extends Item {

    public ShardReceptacleItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.has(ModDataComponents.DIMENSION_DATA.get())) {
            if (!level.isClientSide()) {
                DimensionData data = stack.get(ModDataComponents.DIMENSION_DATA.get());

                int hash = Math.abs(data.name().hashCode());
                int targetX = (hash % 10000) * 200;
                int targetZ = ((hash / 10000) % 10000) * 200;

                if (level instanceof ServerLevel serverLevel) {
                    MinecraftServer server = serverLevel.getServer();
                    ServerLevel voidLevel = server.getLevel(ModDimensions.VOID_LEVEL);
                    if (voidLevel != null && player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.teleportTo(
                                voidLevel,
                                targetX + 0.5,
                                100.0,
                                targetZ + 0.5,
                                Set.of(),
                                serverPlayer.getYRot(),
                                serverPlayer.getXRot(),
                                true
                        );
                        serverPlayer.addEffect(
                                new MobEffectInstance(
                                        MobEffects.SLOW_FALLING,
                                        200,
                                        0,
                                        false,
                                        false
                                )
                        );
                    } else {
                        Constants.LOG.error(
                                "Failed to teleport player: Void dimension ({}) not found or player is invalid.",
                                ModDimensions.VOID_LEVEL
                        );
                    }
                }

                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}

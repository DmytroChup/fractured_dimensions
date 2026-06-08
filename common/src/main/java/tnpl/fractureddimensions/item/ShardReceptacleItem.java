package tnpl.fractureddimensions.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import tnpl.fractureddimensions.registry.ModDataComponents;
import tnpl.fractureddimensions.component.DimensionData;

public class ShardReceptacleItem extends Item {

    public ShardReceptacleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.has(ModDataComponents.DIMENSION_DATA.get())) {
            if (!level.isClientSide()) {
                DimensionData data = stack.get(ModDataComponents.DIMENSION_DATA.get());

                int hash = Math.abs(data.name().hashCode());
                int targetX = (hash % 10000) * 200;
                int targetZ = ((hash / 10000) % 10000) * 200;
                
                player.sendSystemMessage(Component.literal("Initiating warp to: " + data.name() + " [X:" + targetX + " Z:" + targetZ + "]")
                        .withStyle(ChatFormatting.GOLD));
                player.sendSystemMessage(Component.literal("Time limit: " + data.survivalTime() + " minutes.")
                        .withStyle(ChatFormatting.RED));

                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}

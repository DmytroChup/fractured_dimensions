package tnpl.fractureddimensions.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.item.ShardReceptacleItem;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fractured")
                .then(Commands.literal("tp_island")
                        .then(Commands.argument("difficulty", IntegerArgumentType.integer(0, 2))
                                .then(Commands.argument("survivalTime", IntegerArgumentType.integer(1, 60))
                                        .then(Commands.argument("sizeType", IntegerArgumentType.integer(0, 2))
                                                .then(Commands.argument("type", IntegerArgumentType.integer(0, 2))
                                                        .then(Commands.argument("variant", IntegerArgumentType.integer(0, 2))
                                                                .executes(ModCommands::tpIsland)
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int tpIsland(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            
            int difficulty = IntegerArgumentType.getInteger(context, "difficulty");
            int survivalTime = IntegerArgumentType.getInteger(context, "survivalTime");
            int sizeType = IntegerArgumentType.getInteger(context, "sizeType");
            int type = IntegerArgumentType.getInteger(context, "type");
            int variant = IntegerArgumentType.getInteger(context, "variant");

            DimensionData debugData = new DimensionData(
                    "Custom-" + player.getRandom().nextInt(1000),
                    player.getRandom().nextIntBetweenInclusive(1000, 9999),
                    difficulty,
                    survivalTime,
                    sizeType,
                    type,
                    variant
            );

            boolean success = ShardReceptacleItem.teleportToIsland(player, player.level(), debugData);
            if (success) {
                source.sendSuccess(() -> Component.literal("§a[Debug] Teleported to new custom island!"), false);
                return 1;
            } else {
                source.sendFailure(Component.literal("Failed to teleport."));
                return 0;
            }
        }
        return 0;
    }
}

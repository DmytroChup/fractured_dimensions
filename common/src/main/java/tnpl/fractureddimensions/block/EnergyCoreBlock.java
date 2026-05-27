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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        // Выполняем логику только на сервере
        if (!level.isClientSide()) {

            // Пытаемся влить 1000 энергии через наш универсальный интерфейс
            // Передаем сторону, по которой кликнул игрок (hitResult.getDirection())
            long inserted = Services.ENERGY.insertEnergy(level, pos, hitResult.getDirection(), 1000, false);

            // Выводим сообщение в чат для дебага
            if (inserted > 0) {
                player.sendSystemMessage(Component.literal("§aУспешно влито " + inserted + " энергии!"));
            } else {
                player.sendSystemMessage(Component.literal("§cЯдро заряжено на максимум!"));
            }
        }

        // Возвращаем SUCCESS, чтобы рука персонажа дернулась (анимация клика)
        return InteractionResult.SUCCESS;
    }
}

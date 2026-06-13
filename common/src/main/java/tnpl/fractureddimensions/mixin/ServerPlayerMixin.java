package tnpl.fractureddimensions.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnpl.fractureddimensions.registry.ModDimensions;
import tnpl.fractureddimensions.worldgen.IslandManager;

import java.util.UUID;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Unique
    private ServerBossEvent fracturedDimensions$timer;

    @Inject(method = "tick", at = @At("HEAD"))
    private void fracturedDimensions$tick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        Level level = player.level();

        if (level.dimension() == ModDimensions.VOID_LEVEL) {
            if (player.tickCount % 10 == 0) {
                IslandManager manager = IslandManager.getVoidInstance();
                if (manager == null) manager = IslandManager.get((ServerLevel) level);

                var islandEntry = manager.findIslandAt(player.getBlockX(), player.getBlockZ());

                if (islandEntry != null) {
                    if (fracturedDimensions$timer == null) {
                        fracturedDimensions$timer = new ServerBossEvent(
                                UUID.randomUUID(),
                                Component.translatable("gui.fractured_dimensions.timer.remaining"),
                                BossEvent.BossBarColor.BLUE,
                                BossEvent.BossBarOverlay.PROGRESS
                        );
                        fracturedDimensions$timer.addPlayer(player);
                    }

                    long remaining = islandEntry.getValue().ticksRemaining(level.getGameTime());
                    long total = (long) islandEntry.getValue().data().survivalTime() * 60 * 20;
                    float progress = Math.clamp((float) remaining / total, 0.0F, 1.0F);

                    fracturedDimensions$timer.setProgress(progress);

                    long seconds = remaining / 20;
                    long mins = seconds / 60;
                    long secs = seconds % 60;
                    String timeStr = String.format("%02d:%02d", mins, secs);

                    fracturedDimensions$timer.setName(Component.translatable(
                            "gui.fractured_dimensions.timer.island_time",
                            islandEntry.getValue().data().name(), timeStr)
                    );

                    if (progress <= 0.0F) {
                        fracturedDimensions$timer.setColor(BossEvent.BossBarColor.RED);
                        fracturedDimensions$timer.setName(Component.translatable(
                                "gui.fractured_dimensions.timer.island_destroyed",
                                islandEntry.getValue().data().name())
                        );
                    } else if (progress < 0.2F) {
                        fracturedDimensions$timer.setColor(BossEvent.BossBarColor.RED);
                    } else if (progress < 0.5F) {
                        fracturedDimensions$timer.setColor(BossEvent.BossBarColor.YELLOW);
                    } else {
                        fracturedDimensions$timer.setColor(BossEvent.BossBarColor.BLUE);
                    }

                } else {
                    if (fracturedDimensions$timer != null) {
                        fracturedDimensions$timer.removePlayer(player);
                        fracturedDimensions$timer = null;
                    }

                    if (!player.isCreative() && !player.isSpectator() && player.tickCount % 20 == 0) {
                        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1, false, false, true));
                        player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false, true));
                    }
                }
            }
        } else {
            if (fracturedDimensions$timer != null) {
                fracturedDimensions$timer.removePlayer(player);
                fracturedDimensions$timer = null;
            }
        }
    }
}

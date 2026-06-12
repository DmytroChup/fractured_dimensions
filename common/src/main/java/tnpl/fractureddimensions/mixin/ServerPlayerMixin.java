package tnpl.fractureddimensions.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnpl.fractureddimensions.registry.ModDimensions;
import tnpl.fractureddimensions.worldgen.IslandManager;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void fracturedDimensions$tick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        Level level = player.level();
        
        if (level.dimension() == ModDimensions.VOID_LEVEL && !player.isCreative() && !player.isSpectator()) {
            if (player.tickCount % 20 == 0) {
                IslandManager manager = IslandManager.getVoidInstance();
                if (manager == null) manager = IslandManager.get((ServerLevel) level);
                
                var islandEntry = manager.findIslandAt(player.getBlockX(), player.getBlockZ());
                
                if (islandEntry == null) {
                    player.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1, false, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false, true));
                }
            }
        }
    }
}

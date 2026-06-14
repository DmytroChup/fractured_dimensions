package tnpl.fractureddimensions.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnpl.fractureddimensions.registry.ModDimensions;
import tnpl.fractureddimensions.worldgen.IslandDecayHandler;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void fracturedDimensions$tick(BooleanSupplier haveTime, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (level.dimension() == ModDimensions.VOID_LEVEL) {
            IslandDecayHandler.tickDecay(level);
        }
    }
}

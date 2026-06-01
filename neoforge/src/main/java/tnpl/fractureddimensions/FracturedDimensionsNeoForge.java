package tnpl.fractureddimensions;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import tnpl.fractureddimensions.events.client.NeoForgeClientModEvents;

@Mod(Constants.MOD_ID)
public class FracturedDimensionsNeoForge {

    public FracturedDimensionsNeoForge(IEventBus eventBus) {
        CommonClass.init();

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            eventBus.addListener(NeoForgeClientModEvents::registerRenderers);
        }
    }
}
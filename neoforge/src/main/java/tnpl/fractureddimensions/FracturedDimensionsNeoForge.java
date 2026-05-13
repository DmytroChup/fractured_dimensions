package tnpl.fractureddimensions;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class FracturedDimensionsNeoForge {

    public FracturedDimensionsNeoForge(IEventBus eventBus) {
        CommonClass.init();
    }
}
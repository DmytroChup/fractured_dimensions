package tnpl.fractureddimensions;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import tnpl.fractureddimensions.command.ModCommands;
import tnpl.fractureddimensions.events.client.NeoForgeClientModEvents;

@Mod(Constants.MOD_ID)
public class FracturedDimensionsNeoForge {

    public FracturedDimensionsNeoForge(IEventBus eventBus) {
        CommonClass.init();

        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> ModCommands.register(event.getDispatcher()));

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            eventBus.addListener(NeoForgeClientModEvents::registerRenderers);
            eventBus.addListener(NeoForgeClientModEvents::registerScreens);
        }
    }
}
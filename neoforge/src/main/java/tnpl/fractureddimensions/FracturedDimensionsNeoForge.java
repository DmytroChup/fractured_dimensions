package tnpl.fractureddimensions;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import tnpl.fractureddimensions.command.ModCommands;
import tnpl.fractureddimensions.events.client.NeoForgeClientModEvents;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import tnpl.fractureddimensions.entity.PhotospheriqueEntity;
import tnpl.fractureddimensions.registry.ModEntityTypes;

@Mod(Constants.MOD_ID)
public class FracturedDimensionsNeoForge {

    public FracturedDimensionsNeoForge(IEventBus eventBus) {
        CommonClass.init();

        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent event)
                -> ModCommands.register(event.getDispatcher()));

        eventBus.addListener((EntityAttributeCreationEvent event)
                -> event.put(ModEntityTypes.PHOTOSPHERIQUE.get(), PhotospheriqueEntity.createAttributes().build()));

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            eventBus.addListener(NeoForgeClientModEvents::registerRenderers);
            eventBus.addListener(NeoForgeClientModEvents::registerScreens);
        }
    }
}
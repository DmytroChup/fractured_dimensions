package tnpl.fractureddimensions.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.energy.NeoForgeCoreEnergyWrapper;
import tnpl.fractureddimensions.registry.ModBlockEntities;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class NeoForgeCapabilityEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntities.ENERGY_CORE.get(),
                (blockEntity, side) -> new NeoForgeCoreEnergyWrapper(blockEntity)
        );
    }
}

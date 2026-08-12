package tnpl.fractureddimensions.events;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.EnergyPortBlock;
import tnpl.fractureddimensions.block.MeteoricGeneratorBlock;
import tnpl.fractureddimensions.block.PressPartBlock;
import tnpl.fractureddimensions.block.entity.PressBlockEntity;
import tnpl.fractureddimensions.energy.NeoForgeGenericEnergyWrapper;
import tnpl.fractureddimensions.energy.NeoForgePortEnergyWrapper;
import tnpl.fractureddimensions.registry.ModBlockEntities;
import tnpl.fractureddimensions.registry.ModBlocks;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class NeoForgeCapabilityEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntities.ENERGY_CORE.get(),
                (blockEntity, side) -> new NeoForgeGenericEnergyWrapper(blockEntity)
        );

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntities.METEORIC_GENERATOR.get(),
                (blockEntity, side) -> {
                    if (side == blockEntity.getBlockState().getValue(MeteoricGeneratorBlock.FACING)) {
                        return null;
                    }
                    return new NeoForgeGenericEnergyWrapper(blockEntity);
                }
        );

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ModBlockEntities.ENERGY_PORT.get(),
                (blockEntity, side) -> {
                    if (side == blockEntity.getBlockState().getValue(EnergyPortBlock.FACING)) {
                        return new NeoForgePortEnergyWrapper(blockEntity);
                    }
                    return null;
                }
        );

        event.registerBlock(
                Capabilities.Energy.BLOCK,
                (level, pos, state, be, side) -> {
                    if (state.getValue(PressPartBlock.PART) == 2 && side == Direction.UP) {
                        BlockEntity mainBe = level.getBlockEntity(pos.below(2));
                        if (mainBe instanceof PressBlockEntity press) {
                            return new NeoForgeGenericEnergyWrapper(press);
                        }
                    }
                    return null;
                },
                ModBlocks.PRESS_PART.get()
        );
    }
}

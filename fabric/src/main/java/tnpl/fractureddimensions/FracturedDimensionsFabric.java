package tnpl.fractureddimensions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import team.reborn.energy.api.EnergyStorage;
import tnpl.fractureddimensions.block.EnergyPortBlock;
import tnpl.fractureddimensions.command.ModCommands;
import tnpl.fractureddimensions.energy.FabricCoreEnergyWrapper;
import tnpl.fractureddimensions.energy.FabricPortEnergyWrapper;
import tnpl.fractureddimensions.registry.ModBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import tnpl.fractureddimensions.entity.PhotospheriqueEntity;
import tnpl.fractureddimensions.registry.ModEntityTypes;

public class FracturedDimensionsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        FabricDefaultAttributeRegistry.register(ModEntityTypes.PHOTOSPHERIQUE.get(), PhotospheriqueEntity.createAttributes());

        CommandRegistrationCallback.EVENT.register((
                dispatcher,
                 registryAccess,
                 environment
        ) -> ModCommands.register(dispatcher));

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        Identifier.fromNamespaceAndPath(Constants.MOD_ID, "azurite_ore")
                )
        );

        EnergyStorage.SIDED.registerForBlockEntity(
                (blockEntity, direction) -> new FabricCoreEnergyWrapper(blockEntity),
                ModBlockEntities.ENERGY_CORE.get()
        );

        EnergyStorage.SIDED.registerForBlockEntity(
                (blockEntity, direction) -> {
                    if (direction == blockEntity.getBlockState().getValue(EnergyPortBlock.FACING)) {
                        return new FabricPortEnergyWrapper(blockEntity);
                    }
                    return null;
                },
                ModBlockEntities.ENERGY_PORT.get()
        );
    }
}

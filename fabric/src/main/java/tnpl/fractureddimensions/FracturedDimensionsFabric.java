package tnpl.fractureddimensions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;

public class FracturedDimensionsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        Identifier.fromNamespaceAndPath("fractured_dimensions", "azurite_ore")
                )
        );
    }
}

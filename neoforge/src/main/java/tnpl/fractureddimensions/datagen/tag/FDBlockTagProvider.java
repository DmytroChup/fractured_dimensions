package tnpl.fractureddimensions.datagen.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.registry.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class FDBlockTagProvider extends BlockTagsProvider {
    public FDBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.FALLEN_METEORITE.get(),
                        ModBlocks.AZURITE_ORE.get(),
                        ModBlocks.DEEPSLATE_AZURITE_ORE.get(),
                        ModBlocks.SPATIAL_FRAME.get(),
                        ModBlocks.OBSERVER_PLATFORM.get(),
                        ModBlocks.ENERGY_CORE.get(),
                        ModBlocks.ANCHOR_CONTROLLER.get(),
                        ModBlocks.ENERGY_PORT.get(),
                        ModBlocks.DYSON_HULL.get(),
                        ModBlocks.DYSON_CORE.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.FALLEN_METEORITE.get(),
                        ModBlocks.SPATIAL_FRAME.get(),
                        ModBlocks.OBSERVER_PLATFORM.get(),
                        ModBlocks.ENERGY_CORE.get(),
                        ModBlocks.ANCHOR_CONTROLLER.get(),
                        ModBlocks.ENERGY_PORT.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.AZURITE_ORE.get(),
                        ModBlocks.DEEPSLATE_AZURITE_ORE.get(),
                        ModBlocks.DYSON_HULL.get(),
                        ModBlocks.DYSON_CORE.get());
    }
}

package tnpl.fractureddimensions.datagen;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.datagen.loot.FDBlockLootProvider;
import tnpl.fractureddimensions.datagen.recipe.FDRecipeProvider;
import tnpl.fractureddimensions.datagen.tag.FDBlockTagProvider;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class FDDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Server event) {
        event.createProvider(FDBlockTagProvider::new);
        event.createProvider(FDRecipeProvider.Runner::new);

        event.createProvider((output, lookupProvider) ->
                new LootTableProvider(
                        output,
                        Set.of(),
                        List.of(
                                new LootTableProvider.SubProviderEntry(FDBlockLootProvider::new, LootContextParamSets.BLOCK)
                        ),
                        lookupProvider
                )
        );
    }
}

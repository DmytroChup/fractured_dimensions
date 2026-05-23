package tnpl.fractureddimensions.datagen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.registration.RegistryObject;
import tnpl.fractureddimensions.registry.ModBlocks;
import tnpl.fractureddimensions.registry.ModItems;

import java.util.Set;
import java.util.stream.Collectors;

public class FDBlockLootProvider extends BlockLootSubProvider {

    public FDBlockLootProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.SPATIAL_FRAME.get());
        this.dropSelf(ModBlocks.OBSERVER_PLATFORM.get());
        this.dropSelf(ModBlocks.ENERGY_CORE.get());
        this.dropSelf(ModBlocks.ANCHOR_CONTROLLER.get());

        this.add(ModBlocks.FALLEN_METEORITE.get(), block -> LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ModItems.METEORITE_SHARD.get())
                                .when(LootItemRandomChanceCondition.randomChance(0.80F))))

                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ModItems.RAW_STARDUST.get())
                                .when(LootItemRandomChanceCondition.randomChance(0.05F))))
        );

        this.add(ModBlocks.AZURITE_ORE.get(),
                block -> createOreDrop(block, ModItems.RAW_AZURITE.get()));
        this.add(ModBlocks.DEEPSLATE_AZURITE_ORE.get(),
                block -> createOreDrop(block, ModItems.RAW_AZURITE.get()));
    }

    @Override
    protected @NonNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .collect(Collectors.toList());
    }
}

package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.registration.RegistryObject;

public class ModItems {

    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(BuiltInRegistries.ITEM, Constants.MOD_ID);

    public static final RegistryObject<Item, Item> RAW_STARDUST = ITEMS.register("raw_stardust", () -> new Item(itemBuilder("raw_stardust")));
    public static final RegistryObject<Item, Item> METEORITE_SHARD = ITEMS.register("meteorite_shard", () -> new Item(itemBuilder("meteorite_shard")));
    public static final RegistryObject<Item, Item> VOID_ALLOY_INGOT = ITEMS.register("void_alloy_ingot", () -> new Item(itemBuilder("void_alloy_ingot")));
    public static final RegistryObject<Item, Item> SPATIAL_WRENCH = ITEMS.register("spatial_wrench", () -> new Item(itemBuilder("spatial_wrench")));
    public static final RegistryObject<Item, Item> RAW_AZURITE = ITEMS.register("raw_azurite", () -> new Item(itemBuilder("raw_azurite")));
    public static final RegistryObject<Item, Item> AZURITE_INGOT = ITEMS.register("azurite_ingot", () -> new Item(itemBuilder("azurite_ingot")));

    public static final RegistryObject<Item, Item> GLASS_LENS = ITEMS.register("glass_lens", () -> new Item(itemBuilder("glass_lens").stacksTo(1)));
    public static final RegistryObject<Item, Item> CRYSTAL_LENS = ITEMS.register("crystal_lens", () -> new Item(itemBuilder("crystal_lens").stacksTo(1)));
    public static final RegistryObject<Item, Item> WARPED_LENS = ITEMS.register("warped_lens", () -> new Item(itemBuilder("warped_lens").stacksTo(1)));
    public static final RegistryObject<Item, Item> SINGULARITY_LENS = ITEMS.register("singularity_lens", () -> new Item(itemBuilder("singularity_lens").stacksTo(1)));

    private static Item.Properties itemBuilder(String name) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name)));
    }

    public static void init() {
    }
}

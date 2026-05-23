package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.AnchorControllerBlock;
import tnpl.fractureddimensions.block.EnergyCoreBlock;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(BuiltInRegistries.BLOCK, Constants.MOD_ID);

    public static final RegistryObject<Block, Block> FALLEN_METEORITE = registerBlock("fallen_meteorite",
            () -> new Block(blockBuilder("fallen_meteorite")
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(5.0F, 10.0F)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block, Block> AZURITE_ORE = registerBlock("azurite_ore",
            () -> new Block(blockBuilder("azurite_ore")
                    .mapColor(MapColor.STONE)
                    .strength(4.0F, 4.0F)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block, Block> DEEPSLATE_AZURITE_ORE = registerBlock("deepslate_azurite_ore",
            () -> new Block(blockBuilder("deepslate_azurite_ore")
                    .mapColor(MapColor.DEEPSLATE)
                    .strength(5.0F, 4.0F)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block, Block> SPATIAL_FRAME = registerBlock("spatial_frame",
            () -> new Block(blockBuilder("spatial_frame")
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)
                    .strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block, Block> OBSERVER_PLATFORM = registerBlock("observer_platform",
            () -> new Block(blockBuilder("observer_platform")
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)
                    .strength(2.0F, 6.0F)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block, EnergyCoreBlock> ENERGY_CORE = registerBlock("energy_core",
            () -> new EnergyCoreBlock(blockBuilder("energy_core")
                    .mapColor(MapColor.COLOR_CYAN)
                    .lightLevel(state -> 7)
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block, AnchorControllerBlock> ANCHOR_CONTROLLER = registerBlock("anchor_controller",
            () -> new AnchorControllerBlock(blockBuilder("anchor_controller")
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()));

    private static BlockBehaviour.Properties blockBuilder(String name) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name)));
    }

    private static <T extends Block> RegistryObject<Block, T> registerBlock(String name, Supplier<T> blockSupplier) {
        RegistryObject<Block, T> block = BLOCKS.register(name, blockSupplier);

        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name)))));

        return block;
    }

    public static void init() {
    }
}

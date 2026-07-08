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
import tnpl.fractureddimensions.block.EnergyPortBlock;
import tnpl.fractureddimensions.block.MeteoricGeneratorBlock;
import tnpl.fractureddimensions.block.PressBlock;
import tnpl.fractureddimensions.block.PressPartBlock;
import tnpl.fractureddimensions.block.ReturnPortalBlock;
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

    public static final RegistryObject<Block, EnergyPortBlock> ENERGY_PORT = registerBlock("energy_port",
            () -> new EnergyPortBlock(blockBuilder("energy_port")
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block, AnchorControllerBlock> ANCHOR_CONTROLLER = registerBlock("anchor_controller",
            () -> new AnchorControllerBlock(blockBuilder("anchor_controller")
                    .mapColor(MapColor.COLOR_BLUE)
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final RegistryObject<Block, MeteoricGeneratorBlock> METEORIC_GENERATOR = registerBlock("meteoric_generator",
            () -> new MeteoricGeneratorBlock(blockBuilder("meteoric_generator")
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)
                    .strength(3.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(MeteoricGeneratorBlock.LIT) ? 13 : 0)));

    public static final RegistryObject<Block, PressBlock> PRESS = registerBlock("press",
            () -> new PressBlock(blockBuilder("press")
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)
                    .strength(3.5F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()));

    public static final RegistryObject<Block, PressPartBlock> PRESS_PART = registerBlockWithoutItem("press_part",
            () -> new PressPartBlock(blockBuilder("press_part")
                    .mapColor(MapColor.METAL)
                    .sound(SoundType.METAL)
                    .strength(3.5F, 6.0F)
                    .noOcclusion()));

    public static final RegistryObject<Block, ReturnPortalBlock> RETURN_PORTAL = registerBlockWithoutItem("return_portal",
            () -> new ReturnPortalBlock(blockBuilder("return_portal")
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(-1.0F, 3600000.0F)
                    .noOcclusion()
                    .noLootTable()
                    .lightLevel(state -> 10)
                    .sound(SoundType.GLASS)));

    public static final RegistryObject<Block, Block> YELLOW_DWARF_CRUST = registerBlock("yellow_dwarf_crust",
            () -> new Block(blockBuilder("yellow_dwarf_crust")
                    .mapColor(MapColor.COLOR_ORANGE)
                    .sound(SoundType.STONE)
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 3)));

    public static final RegistryObject<Block, Block> YELLOW_DWARF_PLASMA = registerBlock("yellow_dwarf_plasma",
            () -> new Block(blockBuilder("yellow_dwarf_plasma")
                    .mapColor(MapColor.COLOR_YELLOW)
                    .sound(SoundType.STONE)
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 15)));

    public static final RegistryObject<Block, Block> DYSON_HULL = registerBlock("dyson_hull",
            () -> new Block(blockBuilder("dyson_hull")
                    .mapColor(MapColor.COLOR_BLACK)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .strength(5.0F, 1200.0F)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block, Block> DYSON_CORE = registerBlock("dyson_core",
            () -> new Block(blockBuilder("dyson_core")
                    .mapColor(MapColor.COLOR_CYAN)
                    .sound(SoundType.LODESTONE)
                    .strength(6.0F, 10.0F)
                    .lightLevel(state -> 7)
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

    private static <T extends Block> RegistryObject<Block, T> registerBlockWithoutItem(String name, Supplier<T> blockSupplier) {
        return BLOCKS.register(name, blockSupplier);
    }

    public static void init() {
    }
}

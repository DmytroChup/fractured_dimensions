package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.AnchorControllerBlock;
import tnpl.fractureddimensions.block.EnergyCoreBlock;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(BuiltInRegistries.BLOCK, Constants.MOD_ID);

    public static final RegistryObject<Block, Block> FALLEN_METEORITE = registerBlock("fallen_meteorite",
            () -> new Block(blockBuilder("fallen_meteorite").destroyTime(5.0f)));

    public static final RegistryObject<Block, Block> AZURITE_ORE = registerBlock("azurite_ore",
            () -> new Block(blockBuilder("azurite_ore").destroyTime(3.0f)));

    public static final RegistryObject<Block, Block> DEEPSLATE_AZURITE_ORE = registerBlock("deepslate_azurite_ore",
            () -> new Block(blockBuilder("deepslate_azurite_ore").destroyTime(4.5f)));

    public static final RegistryObject<Block, Block> SPATIAL_FRAME = registerBlock("spatial_frame",
            () -> new Block(blockBuilder("spatial_frame").destroyTime(2.0f)));

    public static final RegistryObject<Block, Block> OBSERVER_PLATFORM = registerBlock("observer_platform",
            () -> new Block(blockBuilder("observer_platform").destroyTime(2.0f)));

    public static final RegistryObject<Block, EnergyCoreBlock> ENERGY_CORE = registerBlock("energy_core",
            () -> new EnergyCoreBlock(blockBuilder("energy_core").destroyTime(3.0f)));

    public static final RegistryObject<Block, AnchorControllerBlock> ANCHOR_CONTROLLER = registerBlock("anchor_controller",
            () -> new AnchorControllerBlock(blockBuilder("anchor_controller").destroyTime(3.0f)));

    private static BlockBehaviour.Properties blockBuilder(String name) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name)));
    }

    private static <T extends Block> RegistryObject<Block, T> registerBlock(String name, Supplier<T> blockSupplier) {
        RegistryObject<Block, T> block = BLOCKS.register(name, blockSupplier);

        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name)))));

        return block;
    }

    public static void init() {
    }
}

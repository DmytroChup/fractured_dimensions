package tnpl.fractureddimensions.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import tnpl.fractureddimensions.platform.services.IPlatformHelper;
import tnpl.fractureddimensions.registry.BlockEntityFactory;
import tnpl.fractureddimensions.registry.ModItems;

import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Block... blocks) {
        return new BlockEntityType<>(factory::create, blocks);
    }

    @Override
    public CreativeModeTab buildCreativeTab() {
        return CreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.SINGULARITY_LENS.get()))
                .title(Component.translatable("itemGroup.fractured_dimensions.tab"))
                .displayItems((displayParameters, output) -> {
                    List<ItemStack> stacks = ModItems.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
                    output.acceptAll(stacks);
                }).build();
    }
}
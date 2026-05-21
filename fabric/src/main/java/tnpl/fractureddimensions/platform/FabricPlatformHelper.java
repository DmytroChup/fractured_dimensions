package tnpl.fractureddimensions.platform;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tnpl.fractureddimensions.platform.services.IPlatformHelper;
import tnpl.fractureddimensions.registry.BlockEntityFactory;
import tnpl.fractureddimensions.registry.ModItems;

import java.util.List;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(factory::create, blocks).build();
    }

    @Override
    public CreativeModeTab buildCreativeTab() {
        return FabricCreativeModeTab.builder()
                .icon(() -> new ItemStack(ModItems.SINGULARITY_LENS.get()))
                .title(Component.translatable("itemGroup.fractureddimensions.tab"))
                .displayItems((displayParameters, output) -> {
                    List<ItemStack> stacks = ModItems.ITEMS.getEntries().stream().map(reg -> new ItemStack(reg.get())).toList();
                    output.acceptAll(stacks);
                }).build();
    }
}

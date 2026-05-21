package tnpl.fractureddimensions.platform.services;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tnpl.fractureddimensions.registry.BlockEntityFactory;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BlockEntityFactory<T> factory, Block... blocks);

    CreativeModeTab buildCreativeTab();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }
}
package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.AnchorControllerBlockEntity;
import tnpl.fractureddimensions.block.entity.EnergyCoreBlockEntity;
import tnpl.fractureddimensions.block.entity.EnergyPortBlockEntity;
import tnpl.fractureddimensions.block.entity.MeteoricGeneratorBlockEntity;
import tnpl.fractureddimensions.block.entity.PressBlockEntity;
import tnpl.fractureddimensions.platform.Services;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

public class ModBlockEntities {

    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<?>, BlockEntityType<EnergyCoreBlockEntity>> ENERGY_CORE =
            BLOCK_ENTITIES.register("energy_core",
                    () -> Services.PLATFORM.createBlockEntityType(EnergyCoreBlockEntity::new, ModBlocks.ENERGY_CORE.get()));

    public static final RegistryObject<BlockEntityType<?>, BlockEntityType<EnergyPortBlockEntity>> ENERGY_PORT =
            BLOCK_ENTITIES.register("energy_port",
                    () -> Services.PLATFORM.createBlockEntityType(EnergyPortBlockEntity::new, ModBlocks.ENERGY_PORT.get()));

    public static final RegistryObject<BlockEntityType<?>, BlockEntityType<AnchorControllerBlockEntity>> ANCHOR_CONTROLLER =
            BLOCK_ENTITIES.register("anchor_controller",
                    () -> Services.PLATFORM.createBlockEntityType(AnchorControllerBlockEntity::new, ModBlocks.ANCHOR_CONTROLLER.get()));

    public static final RegistryObject<BlockEntityType<?>, BlockEntityType<MeteoricGeneratorBlockEntity>> METEORIC_GENERATOR =
            BLOCK_ENTITIES.register("meteoric_generator",
                    () -> Services.PLATFORM.createBlockEntityType(MeteoricGeneratorBlockEntity::new, ModBlocks.METEORIC_GENERATOR.get()));

    public static final RegistryObject<BlockEntityType<?>, BlockEntityType<PressBlockEntity>> PRESS =
            BLOCK_ENTITIES.register("press",
                    () -> Services.PLATFORM.createBlockEntityType(PressBlockEntity::new, ModBlocks.PRESS.get()));

    public static void init() {
    }
}

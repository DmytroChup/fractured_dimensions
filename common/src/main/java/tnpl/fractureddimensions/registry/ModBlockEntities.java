package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.AnchorControllerBlockEntity;
import tnpl.fractureddimensions.block.entity.EnergyCoreBlockEntity;
import tnpl.fractureddimensions.platform.Services;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

public class ModBlockEntities {

    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final RegistryObject<BlockEntityType<?>, BlockEntityType<EnergyCoreBlockEntity>> ENERGY_CORE =
            BLOCK_ENTITIES.register("energy_core",
                    () -> Services.PLATFORM.createBlockEntityType(EnergyCoreBlockEntity::new, ModBlocks.ENERGY_CORE.get()));

    public static final RegistryObject<BlockEntityType<?>, BlockEntityType<AnchorControllerBlockEntity>> ANCHOR_CONTROLLER =
            BLOCK_ENTITIES.register("anchor_controller",
                    () -> Services.PLATFORM.createBlockEntityType(AnchorControllerBlockEntity::new, ModBlocks.ANCHOR_CONTROLLER.get()));

    public static void init() {
    }
}

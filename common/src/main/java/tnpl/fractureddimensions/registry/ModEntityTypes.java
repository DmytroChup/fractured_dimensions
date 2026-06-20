package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.entity.PhotospheriqueEntity;
import tnpl.fractureddimensions.entity.projectile.PhotospheriqueRingEntity;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

public class ModEntityTypes {
    public static final RegistrationProvider<EntityType<?>> ENTITY_TYPES = RegistrationProvider.get(Registries.ENTITY_TYPE, Constants.MOD_ID);

    public static final RegistryObject<EntityType<?>, EntityType<PhotospheriqueEntity>> PHOTOSPHERIQUE =
            ENTITY_TYPES.register("photospherique",
                    () -> EntityType.Builder.of(PhotospheriqueEntity::new, MobCategory.MONSTER)
                            .sized(1.0F, 1.0F)
                            .build(
                                    ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Constants.MOD_ID,"photospherique"))
                            ));

    public static final RegistryObject<EntityType<?>, EntityType<PhotospheriqueRingEntity>> PHOTOSPHERIQUE_RING =
            ENTITY_TYPES.register("photospherique_ring",
                    () -> EntityType.Builder.of(PhotospheriqueRingEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(
                                    ResourceKey.create(Registries.ENTITY_TYPE,
                                            Identifier.fromNamespaceAndPath(Constants.MOD_ID,"photospherique_ring"))
                            ));

    public static void init() {
    }
}

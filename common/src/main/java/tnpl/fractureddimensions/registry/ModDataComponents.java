package tnpl.fractureddimensions.registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

public class ModDataComponents {

    public static final RegistrationProvider<DataComponentType<?>> DATA_COMPONENT_TYPES =
            RegistrationProvider.get(BuiltInRegistries.DATA_COMPONENT_TYPE, Constants.MOD_ID);

    public static final RegistryObject<DataComponentType<?>, DataComponentType<DimensionData>> DIMENSION_DATA =
            DATA_COMPONENT_TYPES.register("dimension_data", () ->
            DataComponentType.<DimensionData>builder()
                    .persistent(DimensionData.CODEC)
                    .networkSynchronized(DimensionData.STREAM_CODEC)
                    .build()
    );

    public static void init() {
    }
}

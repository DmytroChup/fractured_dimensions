package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.platform.Services;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

public class ModCreativeTabs {

    public static final RegistrationProvider<CreativeModeTab> TABS = RegistrationProvider.get(BuiltInRegistries.CREATIVE_MODE_TAB, Constants.MOD_ID);

    public static final RegistryObject<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = TABS.register("tab", Services.PLATFORM::buildCreativeTab);

    public static void init() {
    }
}

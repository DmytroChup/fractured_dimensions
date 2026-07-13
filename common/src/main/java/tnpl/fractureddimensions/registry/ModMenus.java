package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.menu.AnchorControllerMenu;
import tnpl.fractureddimensions.block.entity.menu.MeteoricGeneratorMenu;
import tnpl.fractureddimensions.platform.Services;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

public class ModMenus {

    public static final RegistrationProvider<MenuType<?>> MENUS =
            RegistrationProvider.get(BuiltInRegistries.MENU, Constants.MOD_ID);

    public static final RegistryObject<MenuType<?>, MenuType<AnchorControllerMenu>> ANCHOR_CONTROLLER_MENU =
            MENUS.register("anchor_controller", () ->
                    Services.PLATFORM.createMenuType(AnchorControllerMenu::new));

    public static final RegistryObject<MenuType<?>, MenuType<MeteoricGeneratorMenu>> METEORIC_GENERATOR_MENU =
            MENUS.register("meteoric_generator", () ->
                    Services.PLATFORM.createMenuType(MeteoricGeneratorMenu::new));

    public static final RegistryObject<MenuType<?>, MenuType<tnpl.fractureddimensions.block.entity.menu.PressMenu>> PRESS_MENU =
            MENUS.register("press", () ->
                    Services.PLATFORM.createMenuType(tnpl.fractureddimensions.block.entity.menu.PressMenu::new));

    public static void init() {
    }
}

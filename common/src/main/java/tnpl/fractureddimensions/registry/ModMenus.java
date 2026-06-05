package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.menu.AnchorControllerMenu;
import tnpl.fractureddimensions.platform.Services;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

public class ModMenus {

    public static final RegistrationProvider<MenuType<?>> MENUS =
            RegistrationProvider.get(BuiltInRegistries.MENU, Constants.MOD_ID);

    public static final RegistryObject<MenuType<?>, MenuType<AnchorControllerMenu>> ANCHOR_CONTROLLER_MENU =
            MENUS.register("anchor_controller", () ->
                    Services.PLATFORM.createMenuType(AnchorControllerMenu::new));

    public static void init() {
    }
}

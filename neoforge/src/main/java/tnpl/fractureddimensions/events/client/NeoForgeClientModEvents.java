package tnpl.fractureddimensions.events.client;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import tnpl.fractureddimensions.client.gui.AnchorControllerScreen;
import tnpl.fractureddimensions.client.render.block.AnchorControllerRenderer;
import tnpl.fractureddimensions.registry.ModBlockEntities;
import tnpl.fractureddimensions.registry.ModMenus;

public class NeoForgeClientModEvents {
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ANCHOR_CONTROLLER.get(), AnchorControllerRenderer::new);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ANCHOR_CONTROLLER_MENU.get(), AnchorControllerScreen::new);
    }
}

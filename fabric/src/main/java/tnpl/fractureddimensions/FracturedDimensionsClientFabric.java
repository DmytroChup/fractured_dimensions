package tnpl.fractureddimensions;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import tnpl.fractureddimensions.client.gui.AnchorControllerScreen;
import tnpl.fractureddimensions.client.render.block.AnchorControllerRenderer;
import tnpl.fractureddimensions.registry.ModBlockEntities;
import tnpl.fractureddimensions.registry.ModMenus;

public class FracturedDimensionsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenus.ANCHOR_CONTROLLER_MENU.get(), AnchorControllerScreen::new);
        BlockEntityRenderers.register(ModBlockEntities.ANCHOR_CONTROLLER.get(), AnchorControllerRenderer::new);
    }
}

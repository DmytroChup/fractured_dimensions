package tnpl.fractureddimensions;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import tnpl.fractureddimensions.client.gui.AnchorControllerScreen;
import tnpl.fractureddimensions.client.render.block.AnchorControllerRenderer;
import tnpl.fractureddimensions.registry.ModBlockEntities;
import tnpl.fractureddimensions.registry.ModMenus;
import com.geckolib.renderer.GeoEntityRenderer;
import tnpl.fractureddimensions.registry.ModEntityTypes;
import tnpl.fractureddimensions.client.render.entity.EmptyRenderer;

public class FracturedDimensionsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenus.ANCHOR_CONTROLLER_MENU.get(), AnchorControllerScreen::new);
        BlockEntityRenderers.register(ModBlockEntities.ANCHOR_CONTROLLER.get(), AnchorControllerRenderer::new);

        EntityRenderers.register(ModEntityTypes.PHOTOSPHERIQUE.get(), context -> new GeoEntityRenderer<>(context, ModEntityTypes.PHOTOSPHERIQUE.get()));
        EntityRenderers.register(ModEntityTypes.PHOTOSPHERIQUE_RING.get(), context -> new GeoEntityRenderer<>(context, ModEntityTypes.PHOTOSPHERIQUE_RING.get()));
        EntityRenderers.register(ModEntityTypes.DYSON_DRONE.get(), context -> new GeoEntityRenderer<>(context, ModEntityTypes.DYSON_DRONE.get()));
        EntityRenderers.register(ModEntityTypes.DYSON_PROJECTILE.get(), EmptyRenderer::new);
    }
}

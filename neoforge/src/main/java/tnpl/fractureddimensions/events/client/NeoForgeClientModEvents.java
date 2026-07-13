package tnpl.fractureddimensions.events.client;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import tnpl.fractureddimensions.client.gui.AnchorControllerScreen;
import tnpl.fractureddimensions.client.gui.MeteoricGeneratorScreen;
import tnpl.fractureddimensions.client.gui.PressScreen;
import tnpl.fractureddimensions.client.render.block.AnchorControllerRenderer;
import tnpl.fractureddimensions.client.render.block.PressRenderer;
import tnpl.fractureddimensions.registry.ModBlockEntities;
import tnpl.fractureddimensions.registry.ModMenus;
import com.geckolib.renderer.GeoEntityRenderer;
import tnpl.fractureddimensions.registry.ModEntityTypes;
import tnpl.fractureddimensions.client.render.entity.EmptyRenderer;

public class NeoForgeClientModEvents {
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ANCHOR_CONTROLLER.get(), AnchorControllerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PRESS.get(), PressRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.PHOTOSPHERIQUE.get(), context -> new GeoEntityRenderer<>(context, ModEntityTypes.PHOTOSPHERIQUE.get()));
        event.registerEntityRenderer(ModEntityTypes.PHOTOSPHERIQUE_RING.get(), context -> new GeoEntityRenderer<>(context, ModEntityTypes.PHOTOSPHERIQUE_RING.get()));
        event.registerEntityRenderer(ModEntityTypes.DYSON_DRONE.get(), context -> new GeoEntityRenderer<>(context, ModEntityTypes.DYSON_DRONE.get()));
        event.registerEntityRenderer(ModEntityTypes.DYSON_PROJECTILE.get(), EmptyRenderer::new);
    }

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.ANCHOR_CONTROLLER_MENU.get(), AnchorControllerScreen::new);
        event.register(ModMenus.METEORIC_GENERATOR_MENU.get(), MeteoricGeneratorScreen::new);
        event.register(ModMenus.PRESS_MENU.get(), PressScreen::new);
    }
}

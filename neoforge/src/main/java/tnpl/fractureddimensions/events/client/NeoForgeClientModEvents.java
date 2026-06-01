package tnpl.fractureddimensions.events.client;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import tnpl.fractureddimensions.client.render.block.AnchorControllerRenderer;
import tnpl.fractureddimensions.registry.ModBlockEntities;

public class NeoForgeClientModEvents {
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ANCHOR_CONTROLLER.get(), AnchorControllerRenderer::new);
    }
}

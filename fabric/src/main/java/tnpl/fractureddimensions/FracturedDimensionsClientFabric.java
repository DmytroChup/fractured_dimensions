package tnpl.fractureddimensions;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import tnpl.fractureddimensions.client.render.block.AnchorControllerRenderer;
import tnpl.fractureddimensions.registry.ModBlockEntities;

public class FracturedDimensionsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ModBlockEntities.ANCHOR_CONTROLLER.get(), AnchorControllerRenderer::new);
    }
}

package tnpl.fractureddimensions.client.render.block;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import tnpl.fractureddimensions.block.entity.AnchorControllerBlockEntity;

public class AnchorControllerRenderer<R extends BlockEntityRenderState & GeoRenderState>
        extends GeoBlockRenderer<AnchorControllerBlockEntity, R> {

    public AnchorControllerRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new AnchorControllerModel());
    }

}

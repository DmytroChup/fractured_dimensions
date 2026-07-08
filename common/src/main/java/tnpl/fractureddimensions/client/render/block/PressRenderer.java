package tnpl.fractureddimensions.client.render.block;

import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import tnpl.fractureddimensions.block.entity.PressBlockEntity;

public class PressRenderer<R extends BlockEntityRenderState & GeoRenderState>
        extends GeoBlockRenderer<PressBlockEntity, R> {

    public PressRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new PressModel());
    }
}

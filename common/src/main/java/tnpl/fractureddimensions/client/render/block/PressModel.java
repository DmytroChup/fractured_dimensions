package tnpl.fractureddimensions.client.render.block;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.resources.Identifier;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.PressBlockEntity;

public class PressModel extends GeoModel<PressBlockEntity> {

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/press");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/block/press.png");
    }

    @Override
    public Identifier getAnimationResource(PressBlockEntity animatable) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/press");
    }
}

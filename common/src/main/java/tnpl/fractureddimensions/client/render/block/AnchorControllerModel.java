package tnpl.fractureddimensions.client.render.block;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.google.common.reflect.TypeToken;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.AnchorControllerBlock;
import tnpl.fractureddimensions.block.entity.AnchorControllerBlockEntity;
import tnpl.fractureddimensions.block.entity.MultiblockState;

public class AnchorControllerModel extends GeoModel<AnchorControllerBlockEntity> {

    public static final DataTicket<MultiblockState> STATE_TICKET =
            DataTicket.create("multiblock_state", new TypeToken<>() {});

    @Override
    public void addAdditionalStateData(
            AnchorControllerBlockEntity animatable,
            @Nullable Object relatedObject,
            @NonNull GeoRenderState renderState)
    {
        if (animatable.getBlockState().hasProperty(AnchorControllerBlock.MULTIBLOCK_STATE)) {
            renderState.addGeckolibData(STATE_TICKET, animatable.getBlockState().getValue(AnchorControllerBlock.MULTIBLOCK_STATE));
        }
    }

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID,
                "block/anchor_controller");
    }

    @Override
    public @NonNull Identifier getTextureResource(GeoRenderState renderState) {
        MultiblockState state = renderState.getGeckolibData(STATE_TICKET);
        if (state == MultiblockState.IDLE || state == MultiblockState.READY) {
            return Identifier.fromNamespaceAndPath(Constants.MOD_ID,
                    "textures/block/anchor_controller_idle_ready_3d.png");
        }
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID,
                "textures/block/anchor_controller_incomplete_3d.png");
    }

    @Override
    public @NonNull Identifier getAnimationResource(@NonNull AnchorControllerBlockEntity animatable) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID,
                "block/anchor_controller");
    }
}

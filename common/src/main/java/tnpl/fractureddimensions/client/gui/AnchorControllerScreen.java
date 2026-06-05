package tnpl.fractureddimensions.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.menu.AnchorControllerMenu;

public class AnchorControllerScreen extends AbstractContainerScreen<AnchorControllerMenu> {

    private static final Identifier GUI_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/container/anchor_gui.png");

    /** V offset where the texture content begins. */
    private static final int V0 = AnchorControllerMenu.TEX_V_ORIGIN; // 32

    public AnchorControllerScreen(AnchorControllerMenu menu,
                                  Inventory playerInventory,
                                  Component title) {
        super(menu, playerInventory, title,
              AnchorControllerMenu.GUI_WIDTH,
              AnchorControllerMenu.GUI_HEIGHT);

        // Hide the default title / inventory label
        this.titleLabelX     = -9999;
        this.titleLabelY     = -9999;
        this.inventoryLabelX = -9999;
        this.inventoryLabelY = -9999;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics,
                                   int mouseX, int mouseY, float partialTick) {

        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEX,
                x, y,
                0f, V0,
                AnchorControllerMenu.GUI_WIDTH,
                AnchorControllerMenu.GUI_HEIGHT,
                AnchorControllerMenu.TEX_W,
                AnchorControllerMenu.TEX_H);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }
}
package tnpl.fractureddimensions.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.menu.PressMenu;
import tnpl.fractureddimensions.platform.Services;

public class PressScreen extends BaseMachineScreen<PressMenu> {

    private static final Identifier GUI_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/container/press_gui.png");
            
    private static final Identifier HAMMER_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/components/hammer_full.png");

    private static final Identifier ENERGY_BAR_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/components/press_energy_bar_full.png");

    private static final int ENERGY_BAR_X = 41;
    private static final int ENERGY_BAR_Y = 11;
    private static final int ENERGY_BAR_WIDTH = 42;
    private static final int ENERGY_BAR_HEIGHT = 6;

    private static final int HAMMER_X = 198;
    private static final int HAMMER_Y = 20;
    private static final int HAMMER_WIDTH = 34;
    private static final int HAMMER_HEIGHT = 30;

    public PressScreen(PressMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 255, 216);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEX, x, y, 0f, 40f, this.imageWidth, this.imageHeight, 256, 256);

        // Draw Energy Bar (horizontal, fills left to right)
        float energyProgress = this.menu.getEnergyProgress();
        if (energyProgress > 0) {
            int scaledWidth = (int) (ENERGY_BAR_WIDTH * energyProgress);
            
            graphics.blit(RenderPipelines.GUI_TEXTURED, ENERGY_BAR_TEX,
                    x + ENERGY_BAR_X, y + ENERGY_BAR_Y,
                    0f, 0f,
                    scaledWidth, ENERGY_BAR_HEIGHT,
                    ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT);
        }

        // Draw Hammer Progress (vertical, fills bottom to top)
        float progress = this.menu.getHammerProgress();
        if (progress > 0) {
            int scaledHeight = (int) (HAMMER_HEIGHT * progress);
            int yOffset = HAMMER_HEIGHT - scaledHeight;

            graphics.blit(RenderPipelines.GUI_TEXTURED, HAMMER_TEX,
                    x + HAMMER_X, y + HAMMER_Y + yOffset,
                    0f, yOffset,
                    HAMMER_WIDTH, scaledHeight,
                    HAMMER_WIDTH, HAMMER_HEIGHT);
        }

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
        
        int mx = mouseX - this.leftPos;
        int my = mouseY - this.topPos;
        
        if (mx >= ENERGY_BAR_X && mx < ENERGY_BAR_X + ENERGY_BAR_WIDTH && my >= ENERGY_BAR_Y && my < ENERGY_BAR_Y + ENERGY_BAR_HEIGHT) {
            String energyText = formatEnergy(this.menu.getEnergy()) + " / " + formatEnergy(this.menu.getMaxEnergy()) + " " + Services.ENERGY.getEnergyUnit();
            drawTooltip(graphics, energyText, mx, my);
        }
        
        if (mx >= HAMMER_X && mx < HAMMER_X + HAMMER_WIDTH && my >= HAMMER_Y && my < HAMMER_Y + HAMMER_HEIGHT) {
            int percent = (int) (this.menu.getHammerProgress() * 100);
            if (percent > 0) {
                drawTooltip(graphics, percent + "%", mx, my);
            }
        }
    }
}

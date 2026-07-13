package tnpl.fractureddimensions.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.menu.MeteoricGeneratorMenu;
import tnpl.fractureddimensions.platform.Services;

public class MeteoricGeneratorScreen extends BaseMachineScreen<MeteoricGeneratorMenu> {

    private static final Identifier GUI_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/container/meteoric_generator.png");
            
    private static final Identifier FLAME_LIT_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/components/flame_lit.png");

    private static final Identifier ENERGY_BAR_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/components/energy_bar_full.png");

    private static final int ENERGY_BAR_X = 149;
    private static final int ENERGY_BAR_Y = 10;
    private static final int ENERGY_BAR_WIDTH = 17;
    private static final int ENERGY_BAR_HEIGHT = 82;

    private static final int FLAME_X = 65;
    private static final int FLAME_Y = 62;
    private static final int FLAME_WIDTH = 13;
    private static final int FLAME_HEIGHT = 15;

    public MeteoricGeneratorScreen(MeteoricGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 195);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEX, x, y, 0f, 61f, this.imageWidth, this.imageHeight, 256, 256);

        // Draw Energy Bar
        float energyProgress = this.menu.getEnergyProgress();
        if (energyProgress > 0) {
            int scaledHeight = (int) (ENERGY_BAR_HEIGHT * energyProgress);
            int drawY = (y + ENERGY_BAR_Y) + ENERGY_BAR_HEIGHT - scaledHeight;
            
            graphics.blit(RenderPipelines.GUI_TEXTURED, ENERGY_BAR_TEX,
                    x + ENERGY_BAR_X, drawY,
                    0f, (float)(ENERGY_BAR_HEIGHT - scaledHeight),
                    ENERGY_BAR_WIDTH, scaledHeight,
                    ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT);
        }

        // Draw Flame
        float burnProgress = this.menu.getBurnProgress();
        if (burnProgress > 0) {
            int scaledHeight = (int) (FLAME_HEIGHT * burnProgress);
            int drawY = (y + FLAME_Y) + FLAME_HEIGHT - scaledHeight;

            graphics.blit(RenderPipelines.GUI_TEXTURED, FLAME_LIT_TEX,
                    x + FLAME_X, drawY,
                    0f, (float)(FLAME_HEIGHT - scaledHeight),
                    FLAME_WIDTH, scaledHeight,
                    FLAME_WIDTH, FLAME_HEIGHT);
        }

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
        
        int x = this.leftPos;
        int y = this.topPos;
        int mx = mouseX - x;
        int my = mouseY - y;
        
        if (mx >= ENERGY_BAR_X && mx < ENERGY_BAR_X + ENERGY_BAR_WIDTH && my >= ENERGY_BAR_Y && my < ENERGY_BAR_Y + ENERGY_BAR_HEIGHT) {
            String energyText = formatEnergy(this.menu.getEnergy()) + " / " + formatEnergy(this.menu.getMaxEnergy()) + " " + Services.ENERGY.getEnergyUnit();
            drawTooltip(graphics, energyText, mx, my);
        }
        
        if (mx >= FLAME_X && mx < FLAME_X + FLAME_WIDTH && my >= FLAME_Y && my < FLAME_Y + FLAME_HEIGHT) {
            int percent = (int) (this.menu.getBurnProgress() * 100);
            if (percent > 0) {
                drawTooltip(graphics, percent + "%", mx, my);
            }
        }
    }
}

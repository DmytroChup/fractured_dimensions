package tnpl.fractureddimensions.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.menu.MeteoricGeneratorMenu;
import tnpl.fractureddimensions.platform.Services;

public class MeteoricGeneratorScreen extends AbstractContainerScreen<MeteoricGeneratorMenu> {

    private static final Identifier GUI_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/container/meteoric_generator.png");
            
    private static final Identifier FLAME_LIT_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/components/flame_lit.png");

    private static final Identifier ENERGY_BAR_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/components/energy_bar_full.png");

    public MeteoricGeneratorScreen(MeteoricGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 195);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEX, x, y, 0f, 61f, this.imageWidth, this.imageHeight, 256, 256);

        int energyBarX = x + 149;
        int energyBarY = y + 10;

        int flameX = x + 65;
        int flameY = y + 62;

        // Draw Energy Bar
        float energyProgress = this.menu.getEnergyProgress();
        if (energyProgress > 0) {
            int barWidth = 17;
            int barHeight = 82;
            int scaledHeight = (int) (barHeight * energyProgress);

            int drawY = energyBarY + barHeight - scaledHeight;
            
            graphics.blit(RenderPipelines.GUI_TEXTURED, ENERGY_BAR_TEX,
                    energyBarX, drawY,
                    0f, (float)(barHeight - scaledHeight),
                    barWidth, scaledHeight,
                    barWidth, barHeight);
        }

        // Draw Flame
        float burnProgress = this.menu.getBurnProgress();
        if (burnProgress > 0) {
            int flameWidth = 13;
            int flameHeight = 15;
            int scaledHeight = (int) (flameHeight * burnProgress);

            int drawY = flameY + flameHeight - scaledHeight;

            graphics.blit(RenderPipelines.GUI_TEXTURED, FLAME_LIT_TEX,
                    flameX, drawY,
                    0f, (float)(flameHeight - scaledHeight),
                    flameWidth, scaledHeight,
                    flameWidth, flameHeight);
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
        
        if (mx >= 149 && mx < 149 + 17 && my >= 10 && my < 10 + 82) {
            String energyText = formatEnergy(this.menu.getEnergy()) + " / " + formatEnergy(this.menu.getMaxEnergy()) + " " + Services.ENERGY.getEnergyUnit();
            drawTooltip(graphics, energyText, mx, my);
        }
        
        if (mx >= 65 && mx < 65 + 13 && my >= 62 && my < 62 + 15) {
            int percent = (int) (this.menu.getBurnProgress() * 100);
            if (percent > 0) {
                drawTooltip(graphics, percent + "%", mx, my);
            }
        }
    }

    private void drawTooltip(GuiGraphicsExtractor graphics, String text, int mx, int my) {
        int textWidth = this.font.width(text);
        int tooltipX = mx + 12;
        int tooltipY = my - 12;
        graphics.fill(tooltipX - 3, tooltipY - 3, tooltipX + textWidth + 3, tooltipY + 11, 0xDD000000);
        graphics.fill(tooltipX - 4, tooltipY - 4, tooltipX + textWidth + 4, tooltipY - 3, 0xFF555555);
        graphics.fill(tooltipX - 4, tooltipY + 11, tooltipX + textWidth + 4, tooltipY + 12, 0xFF555555);
        graphics.fill(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + 11, 0xFF555555);
        graphics.fill(tooltipX + textWidth + 3, tooltipY - 3, tooltipX + textWidth + 4, tooltipY + 11, 0xFF555555);
        graphics.text(this.font, text, tooltipX, tooltipY, 0xFFFFFFFF, false);
    }

    private String formatEnergy(int number) {
        if (number >= 1_000_000) {
            return String.format(java.util.Locale.US, "%.1fM", number / 1_000_000.0f).replace(".0M", "M");
        } else if (number >= 1_000) {
            return String.format(java.util.Locale.US, "%.1fk", number / 1_000.0f).replace(".0k", "k");
        }
        return String.valueOf(number);
    }
}

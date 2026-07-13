package tnpl.fractureddimensions.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class BaseMachineScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public BaseMachineScreen(T menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight) {
        super(menu, playerInventory, title, imageWidth, imageHeight);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    protected void drawTooltip(GuiGraphicsExtractor graphics, String text, int mx, int my) {
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

    protected String formatEnergy(int number) {
        if (number >= 1_000_000) {
            return String.format(java.util.Locale.US, "%.1fM", number / 1_000_000.0f).replace(".0M", "M");
        } else if (number >= 1_000) {
            return String.format(java.util.Locale.US, "%.1fk", number / 1_000.0f).replace(".0k", "k");
        }
        return String.valueOf(number);
    }
}

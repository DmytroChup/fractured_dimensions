package tnpl.fractureddimensions.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.block.entity.menu.AnchorControllerMenu;

import java.util.ArrayList;
import java.util.List;

public class AnchorControllerScreen extends AbstractContainerScreen<AnchorControllerMenu> {

    private static final Identifier GUI_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/container/anchor_gui.png");
            
    private static final Identifier SPACE_BG = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/container/space_bg.png");

    private static final int V0 = AnchorControllerMenu.TEX_V_ORIGIN;

    private static final int MAP_X = 5;
    private static final int MAP_Y = 5;
    private static final int MAP_W = 206; 
    private static final int MAP_H = 116; 
    
    private final List<CosmicObject> cosmicObjects = new ArrayList<>();
    private CosmicObject selectedObject = null;
    
    private double mapOffsetX = 0;
    private double mapOffsetY = 0;
    private double mapScale = 1.0;
    private boolean mapInitialized = false;

    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private boolean isDraggingMap = false;

    public AnchorControllerScreen(AnchorControllerMenu menu,
                                  Inventory playerInventory,
                                  Component title) {
        super(menu, playerInventory, title,
              AnchorControllerMenu.GUI_WIDTH,
              AnchorControllerMenu.GUI_HEIGHT);

        this.titleLabelX     = -9999;
        this.titleLabelY     = -9999;
        this.inventoryLabelX = -9999;
        this.inventoryLabelY = -9999;
    }

    private void initMapIfNeeded() {
        if (mapInitialized) return;
        mapInitialized = true;
        
        int seed = this.menu.getSeed();
        RandomSource random = RandomSource.create(seed);
        
        cosmicObjects.clear();

        int numClusters = 4 + random.nextInt(3);
        for (int c = 0; c < numClusters; c++) {
            double clusterX = (random.nextDouble() - 0.5) * 2400;
            double clusterY = (random.nextDouble() - 0.5) * 2400;

            int numObjects = 5 + random.nextInt(6); 
            for (int i = 0; i < numObjects; i++) {
                double x = clusterX + random.nextGaussian() * 150;
                double y = clusterY + random.nextGaussian() * 150;
                int type = random.nextInt(3); 
                String[] names = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Sigma", "Omega"};
                String name = names[random.nextInt(names.length)] + "-" + (100 + random.nextInt(900));
                cosmicObjects.add(new CosmicObject(cosmicObjects.size(), x, y, type, name));
            }
        }
    }

    private void clampMapOffset() {
        double maxOffset = 1500 * mapScale;
        this.mapOffsetX = Math.clamp(this.mapOffsetX, -maxOffset, maxOffset);
        this.mapOffsetY = Math.clamp(this.mapOffsetY, -maxOffset, maxOffset);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics,
                                   int mouseX, int mouseY, float partialTick) {
        
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;
        initMapIfNeeded();

        int x = this.leftPos;
        int y = this.topPos;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEX,
                x, y,
                0f, V0,
                AnchorControllerMenu.GUI_WIDTH,
                AnchorControllerMenu.GUI_HEIGHT,
                AnchorControllerMenu.TEX_W,
                AnchorControllerMenu.TEX_H);
                
        int mapScreenX = x + MAP_X;
        int mapScreenY = y + MAP_Y;
        
        graphics.enableScissor(mapScreenX, mapScreenY, mapScreenX + MAP_W, mapScreenY + MAP_H);
        
        int bgSize = 256;
        int bgOffsetX = (int) (mapOffsetX * 0.3) % bgSize; 
        if (bgOffsetX > 0) bgOffsetX -= bgSize;
        
        int bgOffsetY = (int) (mapOffsetY * 0.3) % bgSize;
        if (bgOffsetY > 0) bgOffsetY -= bgSize;

        for (int dx = bgOffsetX; dx < MAP_W; dx += bgSize) {
            for (int dy = bgOffsetY; dy < MAP_H; dy += bgSize) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, SPACE_BG,
                        mapScreenX + dx, mapScreenY + dy,
                        0f, 0f,
                        bgSize, bgSize,
                        bgSize, bgSize);
            }
        }
        
        for (CosmicObject obj : cosmicObjects) {
            double objScreenX = mapScreenX + MAP_W / 2.0 + (obj.x * mapScale) + mapOffsetX;
            double objScreenY = mapScreenY + MAP_H / 2.0 + (obj.y * mapScale) + mapOffsetY;
            
            double renderSize = 2 * mapScale;
            
            if (objScreenX < mapScreenX - 20 || objScreenX > mapScreenX + MAP_W + 20) continue;
            if (objScreenY < mapScreenY - 20 || objScreenY > mapScreenY + MAP_H + 20) continue;
            
            int color;
            if (obj.type == 0) color = 0xFFFFAA;
            else if (obj.type == 1) color = 0xAAFFAA;
            else color = 0xAAAAAA;
            
            if (selectedObject == obj) {
                int highlightSize = (int)(renderSize + 2);
                graphics.fill((int)objScreenX - highlightSize, (int)objScreenY - highlightSize, 
                              (int)objScreenX + highlightSize, (int)objScreenY + highlightSize, 0xFF00FF00);
            }
            graphics.fill((int)objScreenX - (int)renderSize, (int)objScreenY - (int)renderSize, 
                          (int)objScreenX + (int)renderSize, (int)objScreenY + (int)renderSize, color | 0xFF000000);
        }
        
        graphics.disableScissor();

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }
    
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isSecondary) {
        int mapScreenX = this.leftPos + MAP_X;
        int mapScreenY = this.topPos + MAP_Y;
        
        if (!isSecondary && lastMouseX >= mapScreenX && lastMouseX <= mapScreenX + MAP_W && lastMouseY >= mapScreenY && lastMouseY <= mapScreenY + MAP_H) {
            this.isDraggingMap = true;
            
            for (CosmicObject obj : cosmicObjects) {
                double objScreenX = mapScreenX + MAP_W / 2.0 + (obj.x * mapScale) + mapOffsetX;
                double objScreenY = mapScreenY + MAP_H / 2.0 + (obj.y * mapScale) + mapOffsetY;
                
                double hitBox = 4 * Math.max(1.0, mapScale);
                if (lastMouseX >= objScreenX - hitBox && lastMouseX <= objScreenX + hitBox && lastMouseY >= objScreenY - hitBox && lastMouseY <= objScreenY + hitBox) {
                    this.selectedObject = obj;
                }
            }
            return true;
        }
        return super.mouseClicked(event, isSecondary);
    }
    
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.isDraggingMap = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.isDraggingMap) {
            this.mapOffsetX += dragX;
            this.mapOffsetY += dragY;
            clampMapOffset();
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int mapScreenX = this.leftPos + MAP_X;
        int mapScreenY = this.topPos + MAP_Y;
        
        if (mouseX >= mapScreenX && mouseX <= mapScreenX + MAP_W && mouseY >= mapScreenY && mouseY <= mapScreenY + MAP_H) {
            double oldScale = this.mapScale;

            this.mapScale += scrollY * 0.1;
            this.mapScale = Math.clamp(this.mapScale, 0.6, 3.0);

            double logicalX = (mouseX - mapScreenX - MAP_W / 2.0 - mapOffsetX) / oldScale;
            double logicalY = (mouseY - mapScreenY - MAP_H / 2.0 - mapOffsetY) / oldScale;
            
            this.mapOffsetX = (mouseX - mapScreenX - MAP_W / 2.0) - (logicalX * this.mapScale);
            this.mapOffsetY = (mouseY - mapScreenY - MAP_H / 2.0) - (logicalY * this.mapScale);
            
            clampMapOffset();
            return true;
        }
        
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private record CosmicObject(int id, double x, double y, int type, String name) {
    }
}

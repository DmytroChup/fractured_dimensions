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

    private static final Identifier COSMIC_OBJECTS_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/container/cosmic_objects.png");

    private static final Identifier STATIC_NOISE_TEX = Identifier.fromNamespaceAndPath(
            Constants.MOD_ID, "textures/gui/container/static_noise.png");

    private static final int V0 = AnchorControllerMenu.TEX_V_ORIGIN;

    private static final int MAP_X = 3;
    private static final int MAP_Y = 3;
    private static final int MAP_W = 210;
    private static final int MAP_H = 122;
    
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

        int gridSize = 5;
        double cellSize = 2400.0 / gridSize;
        double startX = -1200.0;
        double startY = -1200.0;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                double cellCenterX = startX + (col * cellSize) + (cellSize / 2.0);
                double cellCenterY = startY + (row * cellSize) + (cellSize / 2.0);

                double maxJitter = (cellSize / 2.0) - 60; 
                double x = cellCenterX + (random.nextDouble() * 2 - 1) * maxJitter;
                double y = cellCenterY + (random.nextDouble() * 2 - 1) * maxJitter;

                int type = random.nextInt(3); 
                int variant = random.nextInt(3);
                String[] names = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Sigma", "Omega"};
                String name = names[random.nextInt(names.length)] + "-" + (100 + random.nextInt(900));
                
                int distance = 1000 + random.nextInt(9000);
                int difficulty = random.nextInt(3);
                int[] times = {5, 10, 15};
                int survivalTime = times[random.nextInt(times.length)];
                
                cosmicObjects.add(new CosmicObject(cosmicObjects.size(), x, y, type, variant, name, distance, difficulty, survivalTime));
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
        
        boolean hasLens = this.menu.getSlot(AnchorControllerMenu.LENS_SLOT).hasItem();
        
        if (!hasLens) {
            this.selectedObject = null;
            
            int noiseFrame = (int) (System.currentTimeMillis() / 50);
            int noiseOffsetX = (noiseFrame * 37) % 64;
            int noiseOffsetY = (noiseFrame * 17) % 64;
            if (noiseOffsetX > 0) noiseOffsetX -= 64;
            if (noiseOffsetY > 0) noiseOffsetY -= 64;
            
            for (int dx = noiseOffsetX; dx < MAP_W; dx += 64) {
                for (int dy = noiseOffsetY; dy < MAP_H; dy += 64) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, STATIC_NOISE_TEX,
                            mapScreenX + dx, mapScreenY + dy,
                            0f, 0f,
                            64, 64,
                            64, 64);
                }
            }
            
            if ((System.currentTimeMillis() / 500) % 2 == 0) {
                String errorMsg = "NO LENS DETECTED";
                graphics.centeredText(this.font, errorMsg, mapScreenX + MAP_W / 2, mapScreenY + MAP_H / 2 - 4, 0xFFFF5555);
            }
        } else {
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
                
                int drawSize = (int)(16 * mapScale);
                
                if (objScreenX < mapScreenX - 20 || objScreenX > mapScreenX + MAP_W + 20) continue;
                if (objScreenY < mapScreenY - 20 || objScreenY > mapScreenY + MAP_H + 20) continue;
                
                if (selectedObject == obj) {
                    int hl = (drawSize / 2) + 1;
                    int x1 = (int)objScreenX - hl;
                    int y1 = (int)objScreenY - hl;
                    int x2 = (int)objScreenX + hl;
                    int y2 = (int)objScreenY + hl;
                    int color = 0xFF00FF00;
                    
                    graphics.fill(x1, y1, x2, y1 + 1, color);
                    graphics.fill(x1, y2 - 1, x2, y2, color);
                    graphics.fill(x1, y1, x1 + 1, y2, color);
                    graphics.fill(x2 - 1, y1, x2, y2, color);
                }

                int u = obj.variant * 16;
                int v = obj.type * 16;

                graphics.blit(RenderPipelines.GUI_TEXTURED, COSMIC_OBJECTS_TEX,
                        (int)objScreenX - drawSize / 2, (int)objScreenY - drawSize / 2,
                        u, v,
                        drawSize, drawSize,
                        16, 16,
                        48, 48);
            }
        }
        
        graphics.disableScissor();

        if (hasLens && selectedObject != null) {
            double objScreenX = mapScreenX + MAP_W / 2.0 + (selectedObject.x * mapScale) + mapOffsetX;
            double objScreenY = mapScreenY + MAP_H / 2.0 + (selectedObject.y * mapScale) + mapOffsetY;

            if (objScreenX >= mapScreenX && objScreenX <= mapScreenX + MAP_W &&
                objScreenY >= mapScreenY && objScreenY <= mapScreenY + MAP_H) {
                
                int boxWidth = 140;
                int boxHeight = 55;

                int tooltipX = (int)objScreenX + 15;
                int tooltipY = (int)objScreenY + 15;

                if (tooltipX + boxWidth > mapScreenX + MAP_W) {
                    tooltipX = (int)objScreenX - boxWidth - 15;
                }

                if (tooltipY + boxHeight > mapScreenY + MAP_H) {
                    tooltipY = (int)objScreenY - boxHeight - 15;
                }

                graphics.fill(tooltipX, tooltipY, tooltipX + boxWidth, tooltipY + boxHeight, 0xDD000000);

                graphics.fill(tooltipX, tooltipY, tooltipX + boxWidth, tooltipY + 1, 0xFF555555);
                graphics.fill(tooltipX, tooltipY + boxHeight - 1, tooltipX + boxWidth, tooltipY + boxHeight, 0xFF555555);
                graphics.fill(tooltipX, tooltipY, tooltipX + 1, tooltipY + boxHeight, 0xFF555555);
                graphics.fill(tooltipX + boxWidth - 1, tooltipY, tooltipX + boxWidth, tooltipY + boxHeight, 0xFF555555);

                Component distanceText = Component.translatable("gui.fractured_dimensions.distance", selectedObject.distance);
                Component difficultyText = Component.translatable("gui.fractured_dimensions.difficulty", Component.translatable("gui.fractured_dimensions.difficulty." + selectedObject.difficulty));
                Component survivalText = Component.translatable("gui.fractured_dimensions.survival", selectedObject.survivalTime);

                graphics.text(this.font, selectedObject.name, tooltipX + 5, tooltipY + 5, 0xFFFFFFAA, false);
                graphics.text(this.font, distanceText, tooltipX + 5, tooltipY + 18, 0xFFFFFFFF, false);
                graphics.text(this.font, difficultyText, tooltipX + 5, tooltipY + 30, 0xFFFFFFFF, false);
                graphics.text(this.font, survivalText, tooltipX + 5, tooltipY + 42, 0xFFFFFFFF, false);
            }
        }

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
            
            boolean hasLens = this.menu.getSlot(AnchorControllerMenu.LENS_SLOT).hasItem();
            if (!hasLens) return true;

            for (CosmicObject obj : cosmicObjects) {
                double objScreenX = mapScreenX + MAP_W / 2.0 + (obj.x * mapScale) + mapOffsetX;
                double objScreenY = mapScreenY + MAP_H / 2.0 + (obj.y * mapScale) + mapOffsetY;
                
                double hitBox = 8 * mapScale;
                if (lastMouseX >= objScreenX - hitBox && lastMouseX <= objScreenX + hitBox && lastMouseY >= objScreenY - hitBox && lastMouseY <= objScreenY + hitBox) {
                    if (this.selectedObject == obj) {
                        this.selectedObject = null;
                    } else {
                        this.selectedObject = obj;
                    }
                    break;
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

    private record CosmicObject(int id, double x, double y, int type, int variant, String name, int distance, int difficulty, int survivalTime) {
    }
}

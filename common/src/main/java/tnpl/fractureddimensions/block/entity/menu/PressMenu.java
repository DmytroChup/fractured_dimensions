package tnpl.fractureddimensions.block.entity.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import tnpl.fractureddimensions.registry.ModBlocks;
import tnpl.fractureddimensions.registry.ModMenus;

public class PressMenu extends AbstractContainerMenu {

    public static final int CONTAINER_SIZE = 6;
    public static final int DATA_COUNT = 5;

    public static final int DATA_ENERGY_L = 0;
    public static final int DATA_ENERGY_H = 1;
    public static final int DATA_PROGRESS = 2;
    public static final int DATA_MAX_PROGRESS = 3;
    public static final int DATA_IS_PROCESSING = 4;

    private final Container container;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public PressMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), ContainerLevelAccess.NULL, new SimpleContainerData(DATA_COUNT));
    }

    public PressMenu(int containerId, Inventory playerInventory, Container container, ContainerLevelAccess access, ContainerData data) {
        super(ModMenus.PRESS_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);

        this.container = container;
        this.access = access;
        this.data = data;
        this.addDataSlots(data);

        container.startOpen(playerInventory.player);

        // 5 Input Slots
        this.addSlot(new Slot(container, 0, 57, 68)); // Left
        this.addSlot(new Slot(container, 1, 80, 45)); // Top
        this.addSlot(new Slot(container, 2, 80, 68)); // Center
        this.addSlot(new Slot(container, 3, 80, 91)); // Bottom
        this.addSlot(new Slot(container, 4, 103, 68)); // Right
        
        // Output Slot
        this.addSlot(new Slot(container, 5, 208, 97) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player Inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 134 + row * 18));
            }
        }

        // Player Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 192));
        }
    }

    public int getEnergy() {
        return (this.data.get(DATA_ENERGY_L) & 0xFFFF) | ((this.data.get(DATA_ENERGY_H) & 0xFFFF) << 16);
    }
    public int getMaxEnergy() {
        return 100_000;
    }
    public int getProgress() {
        return this.data.get(DATA_PROGRESS);
    }
    public int getMaxProgress() {
        return this.data.get(DATA_MAX_PROGRESS);
    }

    public float getEnergyProgress() {
        int e = getEnergy();
        int m = getMaxEnergy();
        if (m == 0 || e == 0) return 0f;
        return (float) e / m;
    }

    public float getHammerProgress() {
        int p = getProgress();
        int m = getMaxProgress();
        if (m == 0 || p == 0) return 0f;
        return (float) p / m;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack originalStack = slotStack.copy();

        if (slotIndex < CONTAINER_SIZE) {
            if (!this.moveItemStackTo(slotStack, CONTAINER_SIZE, this.slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (!this.moveItemStackTo(slotStack, 0, 5, false)) return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();

        if (slotStack.getCount() == originalStack.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, slotStack);
        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.PRESS.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}

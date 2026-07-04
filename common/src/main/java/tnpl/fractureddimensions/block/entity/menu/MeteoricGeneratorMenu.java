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
import tnpl.fractureddimensions.registry.ModTags;

public class MeteoricGeneratorMenu extends AbstractContainerMenu {

    public static final int FUEL_SLOT = 0;
    public static final int CONTAINER_SIZE = 1;

    public static final int DATA_ENERGY_L = 0;
    public static final int DATA_ENERGY_H = 1;
    public static final int DATA_MAX_ENERGY_L = 2;
    public static final int DATA_MAX_ENERGY_H = 3;
    public static final int DATA_BURN_TIME_L = 4;
    public static final int DATA_BURN_TIME_H = 5;
    public static final int DATA_MAX_BURN_TIME_L = 6;
    public static final int DATA_MAX_BURN_TIME_H = 7;
    public static final int DATA_COUNT = 8;

    private final Container container;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public MeteoricGeneratorMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SIZE), ContainerLevelAccess.NULL, new SimpleContainerData(DATA_COUNT));
    }

    public MeteoricGeneratorMenu(int containerId, Inventory playerInventory, Container container, ContainerLevelAccess access, ContainerData data) {
        super(ModMenus.METEORIC_GENERATOR_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);

        this.container = container;
        this.access = access;
        this.data = data;
        this.addDataSlots(data);

        container.startOpen(playerInventory.player);

        this.addSlot(new Slot(container, FUEL_SLOT, 64, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModTags.Items.METEORIC_FUELS);
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 113 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 171));
        }
    }

    public int getEnergy() {
        return (this.data.get(DATA_ENERGY_L) & 0xFFFF) | ((this.data.get(DATA_ENERGY_H) & 0xFFFF) << 16);
    }
    public int getMaxEnergy() {
        return (this.data.get(DATA_MAX_ENERGY_L) & 0xFFFF) | ((this.data.get(DATA_MAX_ENERGY_H) & 0xFFFF) << 16);
    }
    public int getBurnTime() {
        return (this.data.get(DATA_BURN_TIME_L) & 0xFFFF) | ((this.data.get(DATA_BURN_TIME_H) & 0xFFFF) << 16);
    }
    public int getMaxBurnTime() {
        return (this.data.get(DATA_MAX_BURN_TIME_L) & 0xFFFF) | ((this.data.get(DATA_MAX_BURN_TIME_H) & 0xFFFF) << 16);
    }

    public float getEnergyProgress() {
        int e = getEnergy();
        int m = getMaxEnergy();
        if (m == 0 || e == 0) return 0f;
        return (float) e / m;
    }

    public float getBurnProgress() {
        int b = getBurnTime();
        int m = getMaxBurnTime();
        if (m == 0 || b == 0) return 0f;
        return (float) b / m;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack originalStack = slotStack.copy();

        if (slotIndex == FUEL_SLOT) {
            if (!this.moveItemStackTo(slotStack, 1, 37, true)) return ItemStack.EMPTY;
        } else {
            if (this.slots.get(FUEL_SLOT).mayPlace(slotStack)) {
                if (!this.moveItemStackTo(slotStack, FUEL_SLOT, FUEL_SLOT + 1, false)) return ItemStack.EMPTY;
            } else if (slotIndex >= 1 && slotIndex < 28) {
                if (!this.moveItemStackTo(slotStack, 28, 37, false)) return ItemStack.EMPTY;
            } else if (slotIndex >= 28 && slotIndex < 37) {
                if (!this.moveItemStackTo(slotStack, 1, 28, false)) return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();

        if (slotStack.getCount() == originalStack.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, slotStack);
        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.METEORIC_GENERATOR.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}

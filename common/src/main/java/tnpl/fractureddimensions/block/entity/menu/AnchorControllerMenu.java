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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.block.entity.AnchorControllerBlockEntity;
import tnpl.fractureddimensions.registry.ModBlocks;
import tnpl.fractureddimensions.registry.ModItems;
import tnpl.fractureddimensions.registry.ModMenus;

import java.util.Set;

public class AnchorControllerMenu extends AbstractContainerMenu {

    // Container slots
    public static final int LENS_SLOT       = 0;
    public static final int RECEPTACLE_SLOT = 1;
    public static final int CONTAINER_SIZE  = 2;

    // ContainerData indices
    public static final int DATA_STATE           = 0;
    public static final int DATA_ENERGY_PERMILLE = 1;
    public static final int DATA_SEED_LOW        = 2;
    public static final int DATA_SEED_HIGH       = 3;
    public static final int DATA_COUNT           = 4;

    // GUI total dimensions (matches the drawn area of the texture)
    public static final int GUI_WIDTH  = 246;
    public static final int GUI_HEIGHT = 224;

    // Texture UV origin (top of the texture's drawn content)
    public static final int TEX_W = 256;
    public static final int TEX_H = 256;
    /** V offset in the texture where the GUI background starts */
    public static final int TEX_V_ORIGIN = 32;

    public static final int LENS_SLOT_X       = 223;
    public static final int LENS_SLOT_Y       = 6;
    public static final int RECEPTACLE_SLOT_X = 223;
    public static final int RECEPTACLE_SLOT_Y = 24;

    private static final int INV_COL_START = 28;
    private static final int INV_ROW_0_Y   = 142;
    private static final int HOTBAR_Y      = 200;

    private static final int PLAYER_INV_START = CONTAINER_SIZE;
    private static final int PLAYER_INV_END   = PLAYER_INV_START + 36;

    // Valid items
    private static final Set<Item> VALID_LENSES = Set.of(
            ModItems.GLASS_LENS.get(),
            ModItems.CRYSTAL_LENS.get(),
            ModItems.WARPED_LENS.get(),
            ModItems.SINGULARITY_LENS.get()
    );

    private final Container container;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    // Constructors

    /** Client-side constructor (used by Fabric/Forge packet deserialization) */
    public AnchorControllerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory,
                new SimpleContainer(CONTAINER_SIZE),
                ContainerLevelAccess.NULL,
                new SimpleContainerData(DATA_COUNT));
    }

    /** Server-side constructor without live ContainerData */
    public AnchorControllerMenu(int containerId, Inventory playerInventory,
                                Container container, ContainerLevelAccess access) {
        this(containerId, playerInventory, container, access,
                new SimpleContainerData(DATA_COUNT));
    }

    /** Full constructor used server-side with real ContainerData */
    public AnchorControllerMenu(int containerId, Inventory playerInventory,
                                Container container, ContainerLevelAccess access,
                                ContainerData data) {
        super(ModMenus.ANCHOR_CONTROLLER_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);

        this.container = container;
        this.access    = access;
        this.data      = data;
        this.addDataSlots(data);

        container.startOpen(playerInventory.player);

        // Slot 0 – Lens (top slot in the right panel)
        this.addSlot(new Slot(container, LENS_SLOT, LENS_SLOT_X, LENS_SLOT_Y) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return isLens(stack);
            }
            @Override
            public int getMaxStackSize() { return 1; }
        });

        // Slot 1 – Shard Receptacle (bottom slot in the right panel)
        this.addSlot(new Slot(container, RECEPTACLE_SLOT, RECEPTACLE_SLOT_X, RECEPTACLE_SLOT_Y) {
            @Override
            public boolean mayPlace(@NonNull ItemStack stack) {
                return stack.is(ModItems.SHARD_RECEPTACLE.get());
            }
            @Override
            public int getMaxStackSize() { return 1; }
        });

        // Player inventory – 3 rows of 9
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory,
                        col + row * 9 + 9,
                        INV_COL_START + col * 18,
                        INV_ROW_0_Y   + row * 18));
            }
        }

        // Hotbar – 1 row of 9
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col,
                    INV_COL_START + col * 18,
                    HOTBAR_Y));
        }
    }

    public static boolean isLens(ItemStack stack) {
        return VALID_LENSES.contains(stack.getItem());
    }

    public int getSeed() {
        return (this.data.get(DATA_SEED_HIGH) << 16) | (this.data.get(DATA_SEED_LOW) & 0xFFFF);
    }

    @Override
    public boolean clickMenuButton(@NonNull Player player, int id) {
        if (this.access != ContainerLevelAccess.NULL) {
            this.access.execute((level, pos) -> {
                if (level.getBlockEntity(pos) instanceof AnchorControllerBlockEntity be) {
                    be.extractObject(id, player);
                }
            });
            return true;
        }
        return false;
    }

    // ── Shift-click logic ──────────────────────────────────────────────────

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack     = slot.getItem();
        ItemStack originalStack = slotStack.copy();

        if (slotIndex < PLAYER_INV_START) {
            if (!this.moveItemStackTo(slotStack, PLAYER_INV_START, PLAYER_INV_END, true))
                return ItemStack.EMPTY;
        } else {
            if (isLens(slotStack)) {
                if (!this.moveItemStackTo(slotStack, LENS_SLOT, LENS_SLOT + 1, false))
                    return ItemStack.EMPTY;
            } else if (slotStack.is(ModItems.SHARD_RECEPTACLE.get())) {
                if (!this.moveItemStackTo(slotStack, RECEPTACLE_SLOT, RECEPTACLE_SLOT + 1, false))
                    return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();

        if (slotStack.getCount() == originalStack.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, slotStack);
        return originalStack;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return stillValid(this.access, player, ModBlocks.ANCHOR_CONTROLLER.get());
    }

    @Override
    public void removed(@NonNull Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() { return this.container; }
}
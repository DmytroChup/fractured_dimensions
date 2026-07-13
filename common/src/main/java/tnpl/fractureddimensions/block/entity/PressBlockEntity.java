package tnpl.fractureddimensions.block.entity;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import com.geckolib.animation.state.AnimationTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import tnpl.fractureddimensions.block.PressBlock;
import tnpl.fractureddimensions.registry.ModBlockEntities;

import java.util.List;

public class PressBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation WORK_ANIM = RawAnimation.begin().thenLoop("work");

    private static final String NBT_INVENTORY = "Inventory";
    private static final String NBT_ENERGY = "Energy";
    private static final String NBT_PROGRESS = "Progress";
    private static final String NBT_MAX_PROGRESS = "MaxProgress";

    public static final int INVENTORY_SIZE = 6; // 5 input, 1 output
    private final SimpleContainer inventory = new SimpleContainer(INVENTORY_SIZE) {
        @Override
        public void setChanged() {
            super.setChanged();
            PressBlockEntity.this.setChanged();
        }
    };

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    private long energy = 0L;
    private final long maxEnergy = 100_000L;
    private int progress = 0;
    private int maxProgress = 0;
    private boolean isProcessing = false;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) (energy & 0xFFFF);
                case 1 -> (int) ((energy >> 16) & 0xFFFF);
                case 2 -> progress;
                case 3 -> maxProgress;
                case 4 -> isProcessing ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            // Read-only on server side
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public PressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PRESS.get(), pos, state);
    }

    public long getEnergy() { return energy; }
    public long getMaxEnergy() { return maxEnergy; }
    
    public void setEnergy(long energy) {
        this.energy = Math.clamp(energy, 0L, this.maxEnergy);
        setChanged();
    }

    public long addEnergy(long amount) {
        if (amount <= 0L) return 0L;
        long space = this.maxEnergy - this.energy;
        long added = Math.min(amount, space);
        this.energy += added;
        setChanged();
        return added;
    }

    public long removeEnergy(long amount) {
        if (amount <= 0L) return 0L;
        long removed = Math.min(amount, this.energy);
        this.energy -= removed;
        setChanged();
        return removed;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationTest<PressBlockEntity> event) {
        if (this.getBlockState().hasProperty(PressBlock.PROCESSING)) {
            boolean processing = this.getBlockState().getValue(PressBlock.PROCESSING);
            if (processing) {
                return event.setAndContinue(WORK_ANIM);
            }
        }
        return PlayState.STOP;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PressBlockEntity be) {
        boolean changed = false;

        if (be.isProcessing) {
            be.progress++;
            
            // TODO: Sound playback logic
            
            if (be.progress >= be.maxProgress) {
                be.progress = 0;
                be.isProcessing = false;
                changed = true;
            }
        }

        boolean visualProcessing = state.getValue(PressBlock.PROCESSING);
        if (visualProcessing != be.isProcessing) {
            level.setBlock(pos, state.setValue(PressBlock.PROCESSING, be.isProcessing), 3);
            changed = true;
        }

        if (changed) {
            be.setChanged();
        }
    }

    public void startProcessing(int time) {
        this.isProcessing = true;
        this.maxProgress = time;
        this.progress = 0;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.setBlock(this.worldPosition, this.getBlockState().setValue(PressBlock.PROCESSING, true), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong(NBT_ENERGY, this.energy);
        output.putInt(NBT_PROGRESS, this.progress);
        output.putInt(NBT_MAX_PROGRESS, this.maxProgress);
        List<ItemStack> savedItems = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            savedItems.set(i, this.inventory.getItem(i));
        }
        output.store(NBT_INVENTORY, ItemStack.OPTIONAL_CODEC.listOf(), savedItems);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = Math.clamp(input.getLongOr(NBT_ENERGY, 0L), 0L, this.maxEnergy);
        this.progress = input.getIntOr(NBT_PROGRESS, 0);
        this.maxProgress = input.getIntOr(NBT_MAX_PROGRESS, 0);
        List<ItemStack> loadedItems = input.read(NBT_INVENTORY, ItemStack.OPTIONAL_CODEC.listOf()).orElse(List.of());
        for (int i = 0; i < loadedItems.size() && i < this.inventory.getContainerSize(); i++) {
            this.inventory.setItem(i, loadedItems.get(i));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.fractured_dimensions.press");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new tnpl.fractureddimensions.block.entity.menu.PressMenu(id, playerInventory, this.inventory, net.minecraft.world.inventory.ContainerLevelAccess.create(this.level, this.worldPosition), this.dataAccess);
    }

}

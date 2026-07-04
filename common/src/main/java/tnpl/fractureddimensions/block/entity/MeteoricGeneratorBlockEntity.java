package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import tnpl.fractureddimensions.platform.Services;
import tnpl.fractureddimensions.registry.ModBlockEntities;
import tnpl.fractureddimensions.registry.ModItems;
import tnpl.fractureddimensions.block.entity.menu.MeteoricGeneratorMenu;
import tnpl.fractureddimensions.registry.ModTags;
import tnpl.fractureddimensions.util.LongDataHelper;

import java.util.List;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

public class MeteoricGeneratorBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {

    private static final String NBT_ENERGY = "Energy";
    private static final String NBT_BURN_TIME = "BurnTime";
    private static final String NBT_MAX_BURN_TIME = "MaxBurnTime";
    private static final String NBT_INVENTORY = "Inventory";

    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private long energy = 0L;
    private final long maxEnergy = 100_000L;
    private int burnTime = 0;
    private int maxBurnTime = 0;
    private final int energyPerTick = 40;

    protected final ContainerData dataAccess = new LongDataHelper(
            new LongSupplier[]{
                    () -> this.energy,
                    () -> this.maxEnergy,
                    () -> this.burnTime,
                    () -> this.maxBurnTime
            },
            new LongConsumer[]{
                    (val) -> this.energy = val,
                    (val) -> {},
                    (val) -> this.burnTime = (int) val,
                    (val) -> this.maxBurnTime = (int) val
            }
    );

    public MeteoricGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.METEORIC_GENERATOR.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.fractured_dimensions.meteoric_generator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new MeteoricGeneratorMenu(
                id,
                playerInventory,
                this,
                ContainerLevelAccess.create(this.level, this.worldPosition), this.dataAccess
        );
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MeteoricGeneratorBlockEntity entity) {
        boolean wasBurning = entity.isBurning();
        boolean changed = false;

        if (entity.isBurning()) {
            entity.burnTime--;
            long space = entity.maxEnergy - entity.energy;
            long generated = Math.min(entity.energyPerTick, space);
            entity.energy += generated;
            changed = true;
        }

        if (!entity.isBurning() && entity.energy < entity.maxEnergy) {
            ItemStack fuel = entity.items.getFirst();
            
            if (fuel.is(ModTags.Items.METEORIC_FUELS)) {
                int fuelValue = level.fuelValues().burnDuration(fuel);

                if (fuelValue <= 0) {
                    if (fuel.is(ModItems.METEORITE_SHARD.get())) fuelValue = 4000;
                    else if (fuel.is(ModItems.RAW_STARDUST.get())) fuelValue = 10000;
                    else fuelValue = 1600;
                }
                fuelValue = Math.max(1, fuelValue / 2);

                entity.burnTime = fuelValue;
                entity.maxBurnTime = fuelValue;
                fuel.shrink(1);
                changed = true;
            }
        }

        if (wasBurning != entity.isBurning()) {
            level.setBlock(pos, state.setValue(BlockStateProperties.LIT, entity.isBurning()), 3);
            changed = true;
        }

        if (entity.energy > 0) {
            long remaining = entity.energy;
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                if (Services.ENERGY.isEnergyContainer(level, neighbor, dir.getOpposite())) {
                    long inserted = Services.ENERGY.insertEnergy(level, neighbor, dir.getOpposite(), remaining, false);
                    remaining -= inserted;
                    changed = true;
                    if (remaining <= 0) break;
                }
            }
            entity.energy = remaining;
        }

        if (changed) {
            entity.setChanged();
        }
    }



    public boolean isBurning() {
        return this.burnTime > 0;
    }

    public long getEnergy() {
        return energy;
    }

    public long getMaxEnergy() {
        return maxEnergy;
    }

    public void setEnergy(long energy) {
        this.energy = Math.clamp(energy, 0L, this.maxEnergy);
        setChanged();
    }

    public long addEnergy(long amount) {
        if (amount <= 0L) return 0L;
        long space = this.maxEnergy - this.energy;
        long added = Math.min(amount, space);
        this.energy += added;
        return added;
    }

    public long removeEnergy(long amount) {
        if (amount <= 0L) return 0L;
        long removed = Math.min(amount, this.energy);
        this.energy -= removed;
        return removed;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong(NBT_ENERGY, this.energy);
        output.putInt(NBT_BURN_TIME, this.burnTime);
        output.putInt(NBT_MAX_BURN_TIME, this.maxBurnTime);
        output.store(NBT_INVENTORY, ItemStack.OPTIONAL_CODEC.listOf(), this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.energy = Math.clamp(input.getLongOr(NBT_ENERGY, 0L), 0L, this.maxEnergy);
        this.burnTime = input.getIntOr(NBT_BURN_TIME, 0);
        this.maxBurnTime = input.getIntOr(NBT_MAX_BURN_TIME, 0);
        
        List<ItemStack> loadedItems = input.read(NBT_INVENTORY, ItemStack.OPTIONAL_CODEC.listOf()).orElse(List.of());
        for (int i = 0; i < loadedItems.size() && i < this.items.size(); i++) {
            this.items.set(i, loadedItems.get(i));
        }
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.is(ModTags.Items.METEORIC_FUELS);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return this.canPlaceItem(index, itemStack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.items.getFirst().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.getFirst();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(0, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }
}

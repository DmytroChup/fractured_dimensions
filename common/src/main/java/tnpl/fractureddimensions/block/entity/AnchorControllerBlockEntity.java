package tnpl.fractureddimensions.block.entity;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import com.geckolib.animation.state.AnimationTest;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import tnpl.fractureddimensions.block.AnchorControllerBlock;
import tnpl.fractureddimensions.block.entity.menu.AnchorControllerMenu;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.registry.ModBlockEntities;
import tnpl.fractureddimensions.registry.ModDataComponents;
import tnpl.fractureddimensions.registry.ModItems;

import java.util.List;

public class AnchorControllerBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation DEPLOY_ANIM = RawAnimation.begin()
            .thenPlay("deploy")
            .thenLoop("idle_ready");

    private static final RawAnimation UNDEPLOY_ANIM = RawAnimation.begin()
            .thenPlay("deploy_back");

    private static final String NBT_STATE = "MultiblockState";
    private static final String NBT_INVENTORY = "Inventory";
    private static final String NBT_SEED_OFFSET = "SeedOffset";

    /** How often the controller re-validates the structure. 40 ticks = 2 seconds */
    private static final int CHECK_INTERVAL = 40;

    private MultiblockState currentState;
    private int tickCounter;
    private int seedOffset = 0;

    private final SimpleContainer inventory = new SimpleContainer(AnchorControllerMenu.CONTAINER_SIZE) {
        @Override
        public void setChanged() {
            super.setChanged();
            AnchorControllerBlockEntity.this.setChanged();
        }
    };

    private MultiblockState clientPrevState = null;
    private boolean isUndeploying = false;

    /**
     * ContainerData that is passed into every menu instance.
     * Slot 0 = state ordinal, slot 1 = energy permille (0–1000).
     * This anonymous class reads live from the block entity, so it is
     * always up-to-date when Minecraft syncs it to the client.
     */
    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case AnchorControllerMenu.DATA_STATE -> currentState.ordinal();
                case AnchorControllerMenu.DATA_ENERGY_PERMILLE -> computeEnergyPermille();
                case AnchorControllerMenu.DATA_SEED_LOW -> (worldPosition.hashCode() + seedOffset) & 0xFFFF;
                case AnchorControllerMenu.DATA_SEED_HIGH -> ((worldPosition.hashCode() + seedOffset) >> 16) & 0xFFFF;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) { /* read-only on server */ }

        @Override
        public int getCount() { return AnchorControllerMenu.DATA_COUNT; }
    };

    public AnchorControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCHOR_CONTROLLER.get(), pos, state);
        this.currentState = MultiblockState.INCOMPLETE;
    }

    public MultiblockState getCurrentState() {
        return currentState;
    }

    public boolean isUndeploying() {
        return isUndeploying;
    }

    public void setCurrentState(MultiblockState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            setChanged();
            syncBlockState();
        }
    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationTest<AnchorControllerBlockEntity> event) {
        if (this.getBlockState().hasProperty(AnchorControllerBlock.MULTIBLOCK_STATE)) {
            MultiblockState mState = this.getBlockState().getValue(AnchorControllerBlock.MULTIBLOCK_STATE);
            
            if (clientPrevState == null) {
                clientPrevState = mState;
            }

            switch (mState) {
                case MultiblockState.READY -> {
                    clientPrevState = mState;
                    isUndeploying = false;
                    return event.setAndContinue(DEPLOY_ANIM);
                }
                case MultiblockState.IDLE, MultiblockState.INCOMPLETE -> {
                    if (clientPrevState == MultiblockState.READY) {
                        isUndeploying = true;
                    }
                    clientPrevState = mState;

                    if (isUndeploying) {
                        if (event.controller().hasAnimationFinished()) {
                            isUndeploying = false;
                            return PlayState.STOP;
                        }
                        return event.setAndContinue(UNDEPLOY_ANIM);
                    }
                }
                default -> {
                    clientPrevState = mState;
                    isUndeploying = false;
                }
            }
        }
        return PlayState.STOP;
    }

    /**
     * Synchronizes the internal MultiblockState with the block's visual BlockState in the world.
     * This causes the blockstate JSON to switch models/textures instantly for the player.
     */
    private void syncBlockState() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        BlockState currentBlockState = this.level.getBlockState(this.worldPosition);

        // Only update if the block is still our controller (safety check)
        if (currentBlockState.getBlock() instanceof AnchorControllerBlock) {
            MultiblockState visualState = currentBlockState.getValue(AnchorControllerBlock.MULTIBLOCK_STATE);

            if (visualState != this.currentState) {
                BlockState newBlockState = currentBlockState.setValue(
                        AnchorControllerBlock.MULTIBLOCK_STATE, this.currentState);
                // Flag 3 = BLOCK_UPDATE (1) | SEND_TO_CLIENT (2) — notifies neighbors and syncs to client
                this.level.setBlock(this.worldPosition, newBlockState, 3);
            }
        }
    }

    public MultiblockValidator.ValidationResult validateStructure() {
        if (this.level == null) {
            return MultiblockValidator.ValidationResult.fail(this.worldPosition, "Level is null");
        }

        MultiblockValidator.ValidationResult result = MultiblockValidator.checkStructure(this.level, this.worldPosition);

        if (result.isValid() && this.currentState == MultiblockState.INCOMPLETE) {
            setCurrentState(MultiblockState.IDLE);
        } else if (!result.isValid() && this.currentState != MultiblockState.INCOMPLETE) {
            setCurrentState(MultiblockState.INCOMPLETE);
        }

        return result;
    }

    /**
     * Periodically re-validates the multiblock structure so that
     * the controller reacts to blocks being broken by the player.
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, AnchorControllerBlockEntity be) {
        be.tickCounter++;

        if (be.tickCounter < CHECK_INTERVAL) {
            return;
        }
        be.tickCounter = 0;

        // Only re-validate when the structure is supposed to be intact
        if (be.currentState == MultiblockState.IDLE || be.currentState == MultiblockState.ACTIVE || be.currentState == MultiblockState.READY) {
            be.validateStructure();
            
            // Validate energy only when the structure is correctly assembled
            if (be.currentState == MultiblockState.IDLE || be.currentState == MultiblockState.READY) {
                be.checkEnergyLevels();
            }
        }
    }

    public void checkEnergyLevels() {
        if (this.level == null) return;

        BlockPos[] corePositions = new BlockPos[]{
            this.worldPosition.offset(2, 1, -1),
            this.worldPosition.offset(-2, 1, -1),
            this.worldPosition.offset(2, 1, 1),
            this.worldPosition.offset(-2, 1, 1),
            this.worldPosition.offset(0, 1, -2)
        };

        long totalEnergy = 0;
        for (BlockPos corePos : corePositions) {
            if (this.level.getBlockEntity(corePos) instanceof EnergyCoreBlockEntity core) {
                totalEnergy += core.getEnergy();
            }
        }

        if (totalEnergy >= 50_000_000L && this.currentState == MultiblockState.IDLE) {
            setCurrentState(MultiblockState.READY);
        } else if (totalEnergy < 50_000_000L && this.currentState == MultiblockState.READY) {
            setCurrentState(MultiblockState.IDLE);
        }
    }

    /**
     * Computes the total energy across all 5 energy cores as a permille value (0–1000).
     * Used by ContainerData to sync energy to the client screen.
     */
    private int computeEnergyPermille() {
        if (this.level == null) return 0;

        BlockPos[] corePositions = {
            this.worldPosition.offset(2, 1, -1),
            this.worldPosition.offset(-2, 1, -1),
            this.worldPosition.offset(2, 1, 1),
            this.worldPosition.offset(-2, 1, 1),
            this.worldPosition.offset(0, 1, -2)
        };

        long totalEnergy = 0;
        long totalMax   = 0;
        for (BlockPos corePos : corePositions) {
            if (this.level.getBlockEntity(corePos) instanceof EnergyCoreBlockEntity core) {
                totalEnergy += core.getEnergy();
                totalMax    += core.getMaxEnergy();
            }
        }
        if (totalMax <= 0) return 0;
        return (int) Math.min(1000L, totalEnergy * 1000L / totalMax);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putString(NBT_STATE, this.currentState.getSerializedName());
        output.putInt(NBT_SEED_OFFSET, this.seedOffset);
        List<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            items.set(i, this.inventory.getItem(i));
        }
        output.store(NBT_INVENTORY, ItemStack.OPTIONAL_CODEC.listOf(), items);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        this.currentState = MultiblockState.fromName(
                input.getStringOr(NBT_STATE, MultiblockState.INCOMPLETE.getSerializedName())
        );
        this.seedOffset = input.getIntOr(NBT_SEED_OFFSET, 0);
        List<ItemStack> items = input.read(NBT_INVENTORY, ItemStack.OPTIONAL_CODEC.listOf()).orElse(List.of());
        for (int i = 0; i < items.size() && i < this.inventory.getContainerSize(); i++) {
            this.inventory.setItem(i, items.get(i));
        }
    }

    // ── MenuProvider ──────────────────────────────────────────────────────

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("container.fractured_dimensions.anchor_controller");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NonNull Inventory playerInventory, @NonNull Player player) {
        return new AnchorControllerMenu(containerId, playerInventory, this.inventory,
                ContainerLevelAccess.create(this.level, this.worldPosition), this.containerData);
    }

    public void extractObject(int id, Player player) {
        ItemStack receptacle = this.inventory.getItem(AnchorControllerMenu.RECEPTACLE_SLOT);
        if (receptacle.isEmpty() || !receptacle.is(ModItems.SHARD_RECEPTACLE.get())) return;
        if (receptacle.has(ModDataComponents.DIMENSION_DATA.get())) return;

        String extractedName = "Unknown";
        int extractedDistance = 0;
        int extractedDifficulty = 0;
        int extractedSurvivalTime = 0;
        int extractedSizeType = 0;

        int gridSize = 5;
        int currentIndex = 0;
        int seed = this.worldPosition.hashCode() + this.seedOffset;
        RandomSource random = RandomSource.create(seed);

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                random.nextDouble(); random.nextDouble(); // x, y
                random.nextInt(3); random.nextInt(3); // type, variant
                String[] names = {"Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Sigma", "Omega"};
                String name = names[random.nextInt(names.length)] + "-" + (100 + random.nextInt(900));
                int distance = 1000 + random.nextInt(9000);
                int difficulty = random.nextInt(3);
                int[] times = {5, 10, 15};
                int survivalTime = times[random.nextInt(times.length)];
                int sizeType = random.nextInt(3);

                if (currentIndex == id) {
                    extractedName = name;
                    extractedDistance = distance;
                    extractedDifficulty = difficulty;
                    extractedSurvivalTime = survivalTime;
                    extractedSizeType = sizeType;
                }
                currentIndex++;
            }
        }

        receptacle.set(ModDataComponents.DIMENSION_DATA.get(), new DimensionData(extractedName, extractedDistance, extractedDifficulty, extractedSurvivalTime, extractedSizeType));
        receptacle.set(DataComponents.LORE, new ItemLore(java.util.List.of(
                Component.literal(extractedName).withStyle(ChatFormatting.GOLD),
                Component.translatable("gui.fractured_dimensions.distance", extractedDistance).withStyle(ChatFormatting.GRAY),
                Component.translatable("gui.fractured_dimensions.difficulty", Component.translatable("gui.fractured_dimensions.difficulty." + extractedDifficulty)).withStyle(ChatFormatting.GRAY),
                Component.translatable("gui.fractured_dimensions.survival", extractedSurvivalTime).withStyle(ChatFormatting.GRAY),
                Component.translatable("gui.fractured_dimensions.size", Component.translatable("gui.fractured_dimensions.size." + extractedSizeType)).withStyle(ChatFormatting.GRAY)
        )));

        this.seedOffset++;
        this.setChanged();
        this.syncBlockState();
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }
}

package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.registry.ModBlockEntities;

public class AnchorControllerBlockEntity extends BlockEntity {

    private static final String NBT_STATE = "MultiblockState";

    /** How often the controller re-validates the structure. 40 ticks = 2 seconds */
    private static final int CHECK_INTERVAL = 40;

    private MultiblockState currentState;
    private int tickCounter;

    public AnchorControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCHOR_CONTROLLER.get(), pos, state);
        this.currentState = MultiblockState.INCOMPLETE;
    }

    public MultiblockState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(MultiblockState newState) {
        if (this.currentState != newState) {
            this.currentState = newState;
            setChanged();
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
        if (be.currentState == MultiblockState.IDLE || be.currentState == MultiblockState.ACTIVE) {
            be.validateStructure();
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putString(NBT_STATE, this.currentState.getSerializedName());
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        this.currentState = MultiblockState.fromName(
                input.getStringOr(NBT_STATE, MultiblockState.INCOMPLETE.getSerializedName())
        );
    }
}

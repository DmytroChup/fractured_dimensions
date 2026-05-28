package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import tnpl.fractureddimensions.registry.ModBlockEntities;

public class AnchorControllerBlockEntity extends BlockEntity {

    private static final String NBT_STATE = "MultiblockState";

    private MultiblockState currentState;

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

        // Возвращаем результат ключу
        return result;
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

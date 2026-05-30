package tnpl.fractureddimensions.block.entity;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

/**
 * Represents the current operational state of a multiblock structure
 */
public enum MultiblockState implements StringRepresentable {

    /** The multiblock is broken or not fully assembled */
    INCOMPLETE("incomplete"),

    /** The multiblock is correctly assembled but idle — waiting for energy */
    IDLE("idle"),

    /** The multiblock is fully charged. The cannon emerges and waits for a trigger */
    READY("ready"),

    /** The multiblock is actively working — tearing dimensions apart */
    ACTIVE("active");

    public static final StringRepresentable.EnumCodec<MultiblockState> CODEC =
            StringRepresentable.fromEnum(MultiblockState::values);

    private final String serializedName;

    MultiblockState(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public @NonNull String getSerializedName() {
        return serializedName;
    }

    /**
     * Resolves a state from its serialized name.
     * Falls back to {@link #INCOMPLETE} if the name is unknown.
     */
    public static MultiblockState fromName(String name) {
        MultiblockState state = CODEC.byName(name);
        return state != null ? state : INCOMPLETE;
    }
}

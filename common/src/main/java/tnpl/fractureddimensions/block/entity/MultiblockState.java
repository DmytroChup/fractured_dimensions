package tnpl.fractureddimensions.block.entity;

/**
 * Represents the current operational state of a multiblock structure
 */
public enum MultiblockState {

    /** The multiblock is broken or not fully assembled */
    INCOMPLETE("incomplete"),

    /** The multiblock is correctly assembled but idle — waiting for energy */
    IDLE("idle"),

    /** The multiblock is actively working — tearing dimensions apart */
    ACTIVE("active");

    private final String serializedName;

    MultiblockState(String serializedName) {
        this.serializedName = serializedName;
    }

    public String getSerializedName() {
        return serializedName;
    }

    /**
     * Resolves a state from its serialized name
     * Falls back to {@link #INCOMPLETE} if the name is unknown
     */
    public static MultiblockState fromName(String name) {
        for (MultiblockState state : values()) {
            if (state.serializedName.equals(name)) {
                return state;
            }
        }
        return INCOMPLETE;
    }
}

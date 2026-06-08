package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import tnpl.fractureddimensions.Constants;

public class ModDimensions {
    public static final ResourceKey<DimensionType> VOID_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "void"));
    public static final ResourceKey<Level> VOID_LEVEL = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "void"));

    public static void init() {
    }
}

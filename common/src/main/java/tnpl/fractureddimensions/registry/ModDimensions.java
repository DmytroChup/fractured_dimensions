package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import tnpl.fractureddimensions.Constants;

import net.minecraft.core.registries.BuiltInRegistries;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.worldgen.VoidChunkGenerator;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class ModDimensions {
    public static final RegistrationProvider<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = RegistrationProvider.get(BuiltInRegistries.CHUNK_GENERATOR, Constants.MOD_ID);

    public static final ResourceKey<DimensionType> VOID_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "void"));
    public static final ResourceKey<Level> VOID_LEVEL = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "void"));

    public static void init() {
        CHUNK_GENERATORS.register("void_generator", () -> VoidChunkGenerator.CODEC);
    }
}

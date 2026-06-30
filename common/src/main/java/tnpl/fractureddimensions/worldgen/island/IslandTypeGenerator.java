package tnpl.fractureddimensions.worldgen.island;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import tnpl.fractureddimensions.component.DimensionData;

public interface IslandTypeGenerator {
    /**
     * Called for each chunk that intersects with the island.
     * Implementations should handle shaping, block placement, and decorations.
     */
    void generateChunk(ChunkAccess chunk, int startX, int startZ, BlockPos center, DimensionData data, long seed, SimplexNoise noise, RandomSource random);
}

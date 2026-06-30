package tnpl.fractureddimensions.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.util.RandomSource;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.worldgen.island.IslandGeneratorRegistry;
import tnpl.fractureddimensions.worldgen.island.IslandTypeGenerator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class VoidChunkGenerator extends ChunkGenerator {

    public static final MapCodec<VoidChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    FixedBiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> (FixedBiomeSource) gen.getBiomeSource())
            ).apply(instance, VoidChunkGenerator::new)
    );

    public VoidChunkGenerator(FixedBiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion worldGenRegion, long l, RandomState randomState,
                             BiomeManager biomeManager, StructureManager structureManager,
                             ChunkAccess chunkAccess) {
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState,
                                                         StructureManager structureManager,
                                                         ChunkAccess chunk) {
        IslandManager islandManager = IslandManager.getVoidInstance();
        if (islandManager == null) {
            return CompletableFuture.completedFuture(chunk);
        }

        int startX = chunk.getPos().x() * 16;
        int startZ = chunk.getPos().z() * 16;

        Map.Entry<BlockPos, IslandManager.ActiveIsland> nearestIsland = islandManager.findIslandAt(startX + 8, startZ + 8);
        
        if (nearestIsland != null) {
            BlockPos center = nearestIsland.getKey();
            DimensionData data = nearestIsland.getValue().data();
            
            long seed = data.name().hashCode();
            RandomSource random = RandomSource.create(seed);
            SimplexNoise noise = new SimplexNoise(random);

            IslandTypeGenerator generator = IslandGeneratorRegistry.getGenerator(data.type(), data.variant());
            if (generator != null) {
                generator.generateChunk(chunk, startX, startZ, center, data, seed, noise, random);
            }
        }
        
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager,
                             RandomState randomState, ChunkAccess chunk) {
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level,
                             RandomState random) {
        return 0;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState random) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getSpawnHeight(LevelHeightAccessor level) {
        return 100;
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public int getMinY() {
        return -64;
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {
    }
}

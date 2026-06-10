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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.util.RandomSource;
import tnpl.fractureddimensions.component.DimensionData;

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

            int baseRadius = 400 + (data.difficulty() * 10);
            int heightLimit = baseRadius / 3;
            
            BlockState stone = Blocks.STONE.defaultBlockState();
            BlockState dirt = Blocks.DIRT.defaultBlockState();
            BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();

            for (int lx = 0; lx < 16; lx++) {
                for (int lz = 0; lz < 16; lz++) {
                    int globalX = startX + lx;
                    int globalZ = startZ + lz;

                    int relX = globalX - center.getX();
                    int relZ = globalZ - center.getZ();

                    double dist2D = Math.sqrt((double) relX * relX + relZ * relZ);
                    if (dist2D > baseRadius + 100) continue;

                    double edgeNoise = noise.getValue(globalX * 0.02, 0, globalZ * 0.02);
                    double maxDist = baseRadius * (0.7 + (edgeNoise + 1.0) / 2.0 * 0.3);
                    if (dist2D > maxDist) continue;

                    double topNoise = noise.getValue(globalX * 0.015, 100, globalZ * 0.015);
                    int topY = center.getY() + (int) (topNoise * 4);

                    double depthFactor = 1.0 - (dist2D / maxDist);
                    double bottomNoise = noise.getValue(globalX * 0.03, -100, globalZ * 0.03);
                    int bottomY = center.getY() + (int) (-heightLimit * depthFactor * (0.6 + (bottomNoise + 1.0) / 2.0 * 0.4));

                    for (int y = bottomY; y <= topY; y++) {
                        if (y >= -64 && y < 320) {
                            BlockPos localPos = new BlockPos(lx, y, lz);
                            if (y == topY) {
                                chunk.setBlockState(localPos, grass, 0);
                            } else if (y >= topY - 3) {
                                chunk.setBlockState(localPos, dirt, 0);
                            } else {
                                chunk.setBlockState(localPos, stone, 0);
                            }
                        }
                    }
                }
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

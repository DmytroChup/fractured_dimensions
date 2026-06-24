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
import tnpl.fractureddimensions.registry.ModBlocks;

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

            int baseRadius = data.getBaseRadius();
            int heightLimit = baseRadius / 3;
            
            BlockState core = Blocks.STONE.defaultBlockState();
            BlockState surface = Blocks.DIRT.defaultBlockState();

            if (data.type() == 0 && data.variant() == 0) {
                surface = ModBlocks.YELLOW_DWARF_CRUST.get().defaultBlockState();
                core = ModBlocks.YELLOW_DWARF_PLASMA.get().defaultBlockState();
            }

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

                    int originalTopY = topY;
                    boolean isYellowDwarf = (data.type() == 0 && data.variant() == 0);
                    
                    int debrisRadius = 0;
                    int debrisCenterX = 0;
                    int debrisCenterZ = 0;
                    
                    if (isYellowDwarf) {
                        int gridSize = (int) Math.max(16, baseRadius * 0.4);
                        int cellX = Math.floorDiv(globalX, gridSize);
                        int cellZ = Math.floorDiv(globalZ, gridSize);
                        
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                int cx = cellX + dx;
                                int cz = cellZ + dz;
                                
                                long cellSeed = seed ^ (cx * 341873128712L + cz * 132897987541L);
                                RandomSource cellRand = RandomSource.create(cellSeed);
                                
                                // 40% chance for a huge 64x64 cell = strictly 3–4 debris pieces per island
                                if (cellRand.nextFloat() < 0.4f) {
                                    int centerX = cx * gridSize + cellRand.nextInt(gridSize);
                                    int centerZ = cz * gridSize + cellRand.nextInt(gridSize);
                                    
                                    double dCenterX = centerX - center.getX();
                                    double dCenterZ = centerZ - center.getZ();
                                    double islandDist = Math.sqrt(dCenterX * dCenterX + dCenterZ * dCenterZ);
                                    
                                    if (islandDist > baseRadius * 0.50 && islandDist < baseRadius * 0.85) {
                                        // Fragment radius: 4 to 7 blocks
                                        int rad = 4 + cellRand.nextInt(4); 
                                        double distToCenter = Math.sqrt(Math.pow(globalX - centerX, 2) + Math.pow(globalZ - centerZ, 2));
                                        
                                        if (distToCenter <= rad) {
                                            debrisRadius = rad;
                                            debrisCenterX = centerX;
                                            debrisCenterZ = centerZ;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    int maxRenderY = originalTopY + debrisRadius;

                    for (int y = bottomY; y <= Math.max(topY, maxRenderY); y++) {
                        if (y >= -64 && y < 320) {
                            BlockPos localPos = new BlockPos(lx, y, lz);
                            
                            boolean placedDebris = false;

                            // Debris Generation
                            if (debrisRadius > 0 && y >= originalTopY - debrisRadius) {
                                double dx = globalX - debrisCenterX;
                                double dy = y - originalTopY;
                                double dz = globalZ - debrisCenterZ;
                                double dist3D = Math.sqrt(dx*dx + dy*dy + dz*dz);

                                double shapeNoise = noise.getValue(globalX * 0.2, y * 0.2, globalZ * 0.2);
                                
                                if (dist3D + shapeNoise * 2.5 <= debrisRadius) {
                                    placedDebris = true;

                                    long blockSeed = seed ^ (globalX * 73128L + y * 13289L + globalZ * 97987L);
                                    RandomSource blockRand = RandomSource.create(blockSeed);

                                    if (dist3D < debrisRadius * 0.5 && blockRand.nextFloat() < 0.10f) {
                                        chunk.setBlockState(localPos, ModBlocks.DYSON_CORE.get().defaultBlockState(), 0);
                                    } else {
                                        chunk.setBlockState(localPos, ModBlocks.DYSON_HULL.get().defaultBlockState(), 0);
                                    }
                                }
                            }

                            if (!placedDebris && y <= originalTopY) {
                                if (y >= originalTopY - 3) {
                                    chunk.setBlockState(localPos, surface, 0);
                                    
                                    if (y == originalTopY && relX == 5 && relZ == 0 && y + 1 < 320) {
                                        BlockPos portalPos = new BlockPos(lx, y + 1, lz);
                                        chunk.setBlockState(portalPos, ModBlocks.RETURN_PORTAL.get().defaultBlockState(), 0);
                                    }
                                } else {
                                    chunk.setBlockState(localPos, core, 0);
                                }
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

package tnpl.fractureddimensions.worldgen.island;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.registry.ModBlocks;

public class AsteroidGenerator extends AbstractIslandGenerator {

    @Override
    protected int calculateTopY(int globalX, int globalZ, BlockPos center, double dist2D, double maxDist, SimplexNoise noise) {
        int baseTopY = super.calculateTopY(globalX, globalZ, center, dist2D, maxDist, noise);

        double bumpMultiplier = 1.0;
        if (dist2D < 12) {
            bumpMultiplier = dist2D / 12.0;
        }

        double bumpNoise = noise.getValue(globalX * 0.02, 200, globalZ * 0.02);
        return baseTopY + (int) (Math.max(0, bumpNoise) * 25 * bumpMultiplier);
    }

    @Override
    protected void generateColumn(ChunkAccess chunk, int lx, int lz, int globalX, int globalZ, BlockPos center, int topY, int bottomY, double dist2D, double maxDist, DimensionData data, long seed, SimplexNoise noise, RandomSource random) {
        BlockState core = Blocks.TUFF.defaultBlockState();
        BlockState surface = Blocks.COBBLED_DEEPSLATE.defaultBlockState();
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState ore = ModBlocks.AZURITE_ORE.get().defaultBlockState();

        for (int y = bottomY; y <= topY; y++) {
            if (y >= -64 && y < 320) {
                BlockPos localPos = new BlockPos(lx, y, lz);
                
                double mixNoise = noise.getValue(globalX * 0.1, y * 0.1, globalZ * 0.1);
                double oreNoise = noise.getValue(globalX * 0.2, y * 0.2, globalZ * 0.2);

                if (y >= topY - 3) {
                    BlockState placedSurface = surface;
                    if (mixNoise > 0.2) {
                        placedSurface = stone;
                    } else if (mixNoise < -0.2) {
                        placedSurface = core;
                    }
                    chunk.setBlockState(localPos, placedSurface, 0);
                } else {
                    BlockState placedCore = core;
                    if (mixNoise < -0.3) {
                        placedCore = stone;
                    }
                    if (oreNoise > 0.88) {
                        placedCore = ore;
                    }
                    chunk.setBlockState(localPos, placedCore, 0);
                }
            }
        }
    }
}

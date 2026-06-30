package tnpl.fractureddimensions.worldgen.island;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.registry.ModBlocks;

public abstract class AbstractIslandGenerator implements IslandTypeGenerator {

    @Override
    public void generateChunk(ChunkAccess chunk, int startX, int startZ, BlockPos center, DimensionData data, long seed, SimplexNoise noise, RandomSource random) {
        int baseRadius = data.getBaseRadius();
        int heightLimit = baseRadius / 3;

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

                if (!shouldGenerateAt(dist2D, maxDist)) continue;

                double topNoise = noise.getValue(globalX * 0.015, 100, globalZ * 0.015);
                int topY = center.getY() + (int) (topNoise * 4);

                double depthFactor = 1.0 - (dist2D / maxDist);
                if (depthFactor < 0) depthFactor = 0;
                
                double bottomNoise = noise.getValue(globalX * 0.03, -100, globalZ * 0.03);
                int bottomY = center.getY() + (int) (-heightLimit * depthFactor * (0.6 + (bottomNoise + 1.0) / 2.0 * 0.4));

                generateColumn(chunk, lx, lz, globalX, globalZ, center, topY, bottomY, dist2D, maxDist, data, seed, noise, random);

                // Place the return portal
                if (relX == 5 && relZ == 0 && topY + 1 >= -64 && topY + 1 < 320) {
                    BlockPos portalPos = new BlockPos(lx, topY + 1, lz);
                    chunk.setBlockState(portalPos, ModBlocks.RETURN_PORTAL.get().defaultBlockState(), 0);
                }
            }
        }
    }

    protected boolean shouldGenerateAt(double dist2D, double maxDist) {
        return dist2D <= maxDist;
    }

    protected abstract void generateColumn(ChunkAccess chunk, int lx, int lz, int globalX, int globalZ, BlockPos center, int topY, int bottomY, double dist2D, double maxDist, DimensionData data, long seed, SimplexNoise noise, RandomSource random);
}

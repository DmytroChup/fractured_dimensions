package tnpl.fractureddimensions.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.util.RandomSource;
import tnpl.fractureddimensions.registry.ModBlocks;

import java.util.List;
import java.util.Map;

public class IslandDecayHandler {

    public static void tickDecay(ServerLevel level) {
        IslandManager manager = IslandManager.getVoidInstance();
        if (manager == null) manager = IslandManager.get(level);

        long currentTick = level.getGameTime();
        List<Map.Entry<BlockPos, IslandManager.ActiveIsland>> decaying = manager.getDecayingIslands(currentTick);

        for (Map.Entry<BlockPos, IslandManager.ActiveIsland> entry : decaying) {
            BlockPos center = entry.getKey();
            IslandManager.ActiveIsland island = entry.getValue();
            double progress = island.decayProgress(currentTick);

            if (progress <= 0.0) continue;

            int baseRadius = island.data().getBaseRadius();
            long seed = island.data().name().hashCode();
            long decayDurationTicks = (long) (island.data().survivalTime() * 60 * 20 * 0.9) + 200;
            double area = Math.PI * baseRadius * baseRadius;
            
            int desiredColumnsPerTick = (int) ((area * 0.6) / decayDurationTicks);
            int columnsPerTick = Math.clamp(desiredColumnsPerTick, 2, 20);
            int columnsDestroyed = 0;

            RandomSource noiseRandom = RandomSource.create(seed);
            SimplexNoise noise = new SimplexNoise(noiseRandom);

            double stableRatio = 1.0 - progress;

            int searchRadius = (int) (baseRadius * 1.2);

            for (int attempt = 0; attempt < columnsPerTick * 50 && columnsDestroyed < columnsPerTick; attempt++) {
                int dx = level.getRandom().nextInt(searchRadius * 2) - searchRadius;
                int dz = level.getRandom().nextInt(searchRadius * 2) - searchRadius;

                int globalX = center.getX() + dx;
                int globalZ = center.getZ() + dz;

                double distSq = (double) dx * dx + (double) dz * dz;

                double minR = Math.pow(stableRatio, 1.5) * baseRadius;

                if (distSq < minR * minR) continue;

                double edgeNoise = noise.getValue(globalX * 0.02, 0, globalZ * 0.02);
                double maxDist = baseRadius * (0.7 + (edgeNoise + 1.0) / 2.0 * 0.3);

                if (distSq > maxDist * maxDist) continue;

                int cx = globalX >> 4;
                int cz = globalZ >> 4;
                if (!level.hasChunk(cx, cz)) continue;

                ChunkAccess chunk = level.getChunk(cx, cz);

                double topNoise = noise.getValue(globalX * 0.015, 100, globalZ * 0.015);
                int topY = Math.min(319, center.getY() + (int) (topNoise * 4));

                double r = Math.sqrt(distSq);
                double depthFactor = 1.0 - (r / maxDist);
                double bottomNoise = noise.getValue(globalX * 0.03, -100, globalZ * 0.03);
                int heightLimit = baseRadius / 3;
                int bottomY = Math.max(-64, center.getY() + (int) (-heightLimit * depthFactor * (0.6 + (bottomNoise + 1.0) / 2.0 * 0.4)));

                boolean columnHadBlocks = false;

                int sweepTopY = Math.min(319, topY + 100);
                for (int y = sweepTopY; y >= bottomY; y--) {
                    BlockPos pos = new BlockPos(globalX & 15, y, globalZ & 15);
                    BlockState state = chunk.getBlockState(pos);

                    if (!state.isAir() && state.getBlock() != ModBlocks.RETURN_PORTAL.get()) {
                        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), 0);
                        level.getChunkSource().blockChanged(new BlockPos(globalX, y, globalZ));
                        columnHadBlocks = true;
                    }
                }

                if (columnHadBlocks) {
                    columnsDestroyed++;
                    if (level.getRandom().nextInt(3) == 0) {
                        level.sendParticles(ParticleTypes.SMOKE,
                                globalX + 0.5, topY + 0.5, globalZ + 0.5,
                                5, 0.5, 2.0, 0.5, 0.02);
                    }
                }
            }
        }
    }
}

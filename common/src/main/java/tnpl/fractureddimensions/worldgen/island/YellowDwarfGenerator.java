package tnpl.fractureddimensions.worldgen.island;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.registry.ModBlocks;

public class YellowDwarfGenerator extends AbstractIslandGenerator {

    @Override
    protected void generateColumn(
            ChunkAccess chunk,
            int lx,
            int lz,
            int globalX,
            int globalZ,
            BlockPos center,
            int topY,
            int bottomY,
            double dist2D,
            double maxDist,
            DimensionData data,
            long seed,
            SimplexNoise noise,
            RandomSource random
    ) {
        BlockState surface = ModBlocks.YELLOW_DWARF_CRUST.get().defaultBlockState();
        BlockState core = ModBlocks.YELLOW_DWARF_PLASMA.get().defaultBlockState();

        int baseRadius = data.getBaseRadius();
        int debrisRadius = 0;
        int debrisCenterX = 0;
        int debrisCenterZ = 0;

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
                    int cX = cx * gridSize + cellRand.nextInt(gridSize);
                    int cZ = cz * gridSize + cellRand.nextInt(gridSize);

                    double dCenterX = cX - center.getX();
                    double dCenterZ = cZ - center.getZ();
                    double islandDist = Math.sqrt(dCenterX * dCenterX + dCenterZ * dCenterZ);

                    if (islandDist > baseRadius * 0.50 && islandDist < baseRadius * 0.85) {
                        int rad = 4 + cellRand.nextInt(4);
                        double distToCenter = Math.sqrt(Math.pow(globalX - cX, 2) + Math.pow(globalZ - cZ, 2));

                        if (distToCenter <= rad) {
                            debrisRadius = rad;
                            debrisCenterX = cX;
                            debrisCenterZ = cZ;
                        }
                    }
                }
            }
        }

        int maxRenderY = topY + debrisRadius;

        for (int y = bottomY; y <= Math.max(topY, maxRenderY); y++) {
            if (y >= -64 && y < 320) {
                BlockPos localPos = new BlockPos(lx, y, lz);
                boolean placedDebris = false;

                if (debrisRadius > 0 && y >= topY - debrisRadius) {
                    double dy = y - topY;
                    double dxDist = globalX - debrisCenterX;
                    double dzDist = globalZ - debrisCenterZ;
                    double dist3D = Math.sqrt(dxDist * dxDist + dy * dy + dzDist * dzDist);

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

                if (!placedDebris && y <= topY) {
                    if (y >= topY - 3) {
                        chunk.setBlockState(localPos, surface, 0);
                    } else {
                        chunk.setBlockState(localPos, core, 0);
                    }
                }
            }
        }
    }
}

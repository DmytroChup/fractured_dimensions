package tnpl.fractureddimensions.worldgen.island;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import tnpl.fractureddimensions.component.DimensionData;

public class PurplePlanetGenerator extends AbstractIslandGenerator {

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
        BlockState surface = Blocks.PURPUR_BLOCK.defaultBlockState();
        BlockState core = Blocks.END_STONE.defaultBlockState();

        for (int y = bottomY; y <= topY; y++) {
            if (y >= -64 && y < 320) {
                BlockPos localPos = new BlockPos(lx, y, lz);
                
                if (y >= topY - 3) {
                    BlockState placedSurface = surface;
                    if (noise.getValue(globalX * 0.1, y * 0.1, globalZ * 0.1) > 0.3) {
                        placedSurface = Blocks.WARPED_WART_BLOCK.defaultBlockState();
                    }
                    chunk.setBlockState(localPos, placedSurface, 0);
                } else {
                    chunk.setBlockState(localPos, core, 0);
                }
            }
        }
    }
}

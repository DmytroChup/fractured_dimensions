package tnpl.fractureddimensions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;
import tnpl.fractureddimensions.registry.ModBlocks;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class MultiblockValidator {
    private MultiblockValidator() {
        // Utility class — no instantiation
    }

    public record StructureOffset(int dx, int dy, int dz, Supplier<? extends Block> expectedBlock, String label) {
    }

    public record ValidationResult(boolean isValid, @Nullable BlockPos errorPos, @Nullable String missingLabel) {
        public static ValidationResult success() {
            return new ValidationResult(true, null, null);
        }
        public static ValidationResult fail(BlockPos pos, String label) {
            return new ValidationResult(false, pos, label);
        }
    }

    private static final List<StructureOffset> ANCHOR_OFFSETS = buildOffsets();

    private static List<StructureOffset> buildOffsets() {
        List<StructureOffset> list = new java.util.ArrayList<>();

        // Y = 0: 7x7 foundation (OBSERVER_PLATFORM) without corner blocks
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if ((x == 0 && z == 0) || (Math.abs(x) == 3 && Math.abs(z) == 3)) {
                    continue;
                }

                list.add(new StructureOffset(x, 0, z, ModBlocks.OBSERVER_PLATFORM, String.format("Y=0 Base [x=%d, z=%d]", x, z)));
            }
        }

        // Y = 1: Internal energy storage (5x ENERGY_CORE)
        list.add(new StructureOffset( 2, 1, -1, ModBlocks.ENERGY_CORE, "Y=1 Energy Core NE"));
        list.add(new StructureOffset(-2, 1, -1, ModBlocks.ENERGY_CORE, "Y=1 Energy Core NW"));
        list.add(new StructureOffset( 2, 1,  1, ModBlocks.ENERGY_CORE, "Y=1 Energy Core SE"));
        list.add(new StructureOffset(-2, 1,  1, ModBlocks.ENERGY_CORE, "Y=1 Energy Core SW"));
        list.add(new StructureOffset( 0, 1, -2, ModBlocks.ENERGY_CORE, "Y=1 Energy Core Back Center"));

        // Y = 1: Outer U-shaped ring (SPATIAL_FRAME)
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if ((Math.abs(x) == 3 && Math.abs(z) == 3) || (z == 3 && Math.abs(x) <= 1)) {
                    continue;
                }

                if (Math.abs(x) == 3 || Math.abs(z) == 3) {
                    if (x == 0 && z == -3) {
                        list.add(new StructureOffset(0, 1, -3, ModBlocks.ENERGY_PORT, "Y=1 Energy Port"));
                    } else {
                        list.add(new StructureOffset(x, 1, z, ModBlocks.SPATIAL_FRAME, String.format("Y=1 Pillar [x=%d, z=%d]", x, z)));
                    }
                }
            }
        }

        // Y = 2: Outer U-shaped ring (SPATIAL_FRAME + GLASS)
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if ((Math.abs(x) == 3 && Math.abs(z) == 3) || (z == 3 && Math.abs(x) <= 1)) {
                    continue;
                }

                if (Math.abs(x) == 3 || Math.abs(z) == 3) {
                    boolean isGlass = (z == -3 && Math.abs(x) <= 1) || (Math.abs(x) == 3 && z == 0);

                    Supplier<? extends Block> block = isGlass ? () -> Blocks.GLASS : ModBlocks.SPATIAL_FRAME;
                    String type = isGlass ? "Glass" : "Frame";

                    list.add(new StructureOffset(x, 2, z, block, String.format("Y=2 Wall %s [x=%d, z=%d]", type, x, z)));
                }
            }
        }

        // Y = 3: Tapered dome ring (SPATIAL_FRAME)
        for (int x = -1; x <= 1; x++) {
            list.add(new StructureOffset(x, 3, -3, ModBlocks.SPATIAL_FRAME, String.format("Y=3 Back Wall [x=%d, z=-3]", x)));
        }

        for (int z = -1; z <= 1; z++) {
            list.add(new StructureOffset(-3, 3, z, ModBlocks.SPATIAL_FRAME, String.format("Y=3 Left Wall [x=-3, z=%d]", z)));
            list.add(new StructureOffset( 3, 3, z, ModBlocks.SPATIAL_FRAME, String.format("Y=3 Right Wall [x=3, z=%d]", z)));
        }

        list.add(new StructureOffset(-2, 3, -2, ModBlocks.SPATIAL_FRAME, "Y=3 Inner Corner NW"));
        list.add(new StructureOffset( 2, 3, -2, ModBlocks.SPATIAL_FRAME, "Y=3 Inner Corner NE"));
        list.add(new StructureOffset(-2, 3,  2, ModBlocks.SPATIAL_FRAME, "Y=3 Inner Corner SW"));
        list.add(new StructureOffset( 2, 3,  2, ModBlocks.SPATIAL_FRAME, "Y=3 Inner Corner SE"));

        // Y = 4: The dome's outer ring (SPATIAL_FRAME)
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if ((Math.abs(x) == 2 && Math.abs(z) == 2) || (Math.abs(x) <= 1 && Math.abs(z) <= 1)) {
                    continue;
                }

                list.add(new StructureOffset(x, 4, z, ModBlocks.SPATIAL_FRAME, String.format("Y=4 Top Ring [x=%d, z=%d]", x, z)));
            }
        }

        return Collections.unmodifiableList(list);
    }

    public static ValidationResult checkStructure(Level level, BlockPos controllerPos) {
        ValidationResult lastError = null;

        for (int rotation = 0; rotation < 4; rotation++) {
            boolean currentValid = true;
            for (StructureOffset offset : ANCHOR_OFFSETS) {
                int rDx = offset.dx();
                int rDz = offset.dz();

                if (rotation == 1) {
                    rDx = -offset.dz();
                    rDz = offset.dx();
                } else if (rotation == 2) {
                    rDx = -offset.dx();
                    rDz = -offset.dz();
                } else if (rotation == 3) {
                    rDx = offset.dz();
                    rDz = -offset.dx();
                }

                BlockPos checkPos = controllerPos.offset(rDx, offset.dy(), rDz);
                Block expectedBlock = offset.expectedBlock().get();

                if (!level.getBlockState(checkPos).is(expectedBlock)) {
                    lastError = ValidationResult.fail(checkPos, offset.label());
                    currentValid = false;
                    break;
                }
            }
            if (currentValid) {
                return ValidationResult.success();
            }
        }
        return lastError;
    }

    public static @Nullable BlockPos findControllerFrom(Level level, BlockPos clickedPos) {
        for (int rotation = 0; rotation < 4; rotation++) {
            for (StructureOffset offset : ANCHOR_OFFSETS) {
                int rDx = offset.dx();
                int rDz = offset.dz();

                if (rotation == 1) {
                    rDx = -offset.dz();
                    rDz = offset.dx();
                } else if (rotation == 2) {
                    rDx = -offset.dx();
                    rDz = -offset.dz();
                } else if (rotation == 3) {
                    rDx = offset.dz();
                    rDz = -offset.dx();
                }

                BlockPos candidatePos = clickedPos.offset(-rDx, -offset.dy(), -rDz);

                if (level.getBlockState(candidatePos).is(ModBlocks.ANCHOR_CONTROLLER.get())) {
                    return candidatePos;
                }
            }
        }
        return null;
    }
}


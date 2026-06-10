package tnpl.fractureddimensions.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.component.DimensionData;
import tnpl.fractureddimensions.registry.ModDimensions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class IslandManager extends SavedData {

    /**
     * Grid spacing between island centers (in blocks).
     */
    private static final int GRID_SPACING = 1200;

    private static final int ISLAND_Y = 100;

    /** Static reference to the void dimension's manager, so ChunkGenerator can access it */
    private static final AtomicReference<IslandManager> voidInstance = new AtomicReference<>();

    public record ActiveIsland(DimensionData data, long createdTick) {
        public static final Codec<ActiveIsland> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                DimensionData.CODEC.fieldOf("data").forGetter(ActiveIsland::data),
                Codec.LONG.fieldOf("createdTick").forGetter(ActiveIsland::createdTick)
        ).apply(instance, ActiveIsland::new));

        public long expirationTick() {
            return createdTick + (long) data.survivalTime() * 60 * 20;
        }

        public long ticksRemaining(long currentTick) {
            return Math.max(0, expirationTick() - currentTick);
        }

        public boolean isExpired(long currentTick) {
            return currentTick >= expirationTick();
        }

        public double decayProgress(long currentTick) {
            long totalTicks = (long) data.survivalTime() * 60 * 20;
            long elapsed = currentTick - createdTick;
            long decayStart = (long) (totalTicks * 0.7);

            if (elapsed < decayStart) return 0.0;
            if (elapsed >= totalTicks) return 1.0;

            return (double) (elapsed - decayStart) / (totalTicks - decayStart);
        }
    }

    /** Instance map using ConcurrentHashMap to be safe with async chunk generation */
    private final Map<BlockPos, ActiveIsland> islands = new ConcurrentHashMap<>();

    private int nextIndex = 0;

    private static final Codec<Map<BlockPos, ActiveIsland>> ISLANDS_MAP_CODEC =
            Codec.unboundedMap(Codec.STRING, ActiveIsland.CODEC).xmap(
                    raw -> {
                        Map<BlockPos, ActiveIsland> result = new ConcurrentHashMap<>();
                        for (Map.Entry<String, ActiveIsland> entry : raw.entrySet()) {
                            String[] parts = entry.getKey().split(",");
                            if (parts.length == 3) {
                                result.put(
                                        new BlockPos(
                                                Integer.parseInt(parts[0].trim()),
                                                Integer.parseInt(parts[1].trim()),
                                                Integer.parseInt(parts[2].trim())
                                        ),
                                        entry.getValue()
                                );
                            }
                        }
                        return result;
                    },
                    map -> {
                        Map<String, ActiveIsland> result = new HashMap<>();
                        for (Map.Entry<BlockPos, ActiveIsland> entry : map.entrySet()) {
                            BlockPos pos = entry.getKey();
                            result.put(pos.getX() + "," + pos.getY() + "," + pos.getZ(), entry.getValue());
                        }
                        return result;
                    }
            );

    public static final Codec<IslandManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ISLANDS_MAP_CODEC.fieldOf("islands").forGetter(m -> m.islands),
            Codec.INT.fieldOf("nextIndex").forGetter(m -> m.nextIndex)
    ).apply(instance, (islandsMap, nextIdx) -> {
        IslandManager manager = new IslandManager();
        manager.islands.putAll(islandsMap);
        manager.nextIndex = nextIdx;
        return manager;
    }));

    public IslandManager() {
    }

    public synchronized BlockPos allocateNextPosition() {
        int[] coords = spiralCoords(nextIndex);
        nextIndex++;
        setDirty();
        return new BlockPos(coords[0] * GRID_SPACING, ISLAND_Y, coords[1] * GRID_SPACING);
    }

    private static int[] spiralCoords(int index) {
        if (index == 0) return new int[]{0, 0};

        int layer = 1;
        int count = 1;
        while (count + 8 * layer <= index) {
            count += 8 * layer;
            layer++;
        }

        int pos = index - count;
        int side = pos / (2 * layer);
        int offset = pos % (2 * layer);

        return switch (side) {
            case 0 -> new int[]{layer, -layer + 1 + offset};
            case 1 -> new int[]{layer - 1 - offset, layer};
            case 2 -> new int[]{-layer, layer - 1 - offset};
            case 3 -> new int[]{-layer + 1 + offset, -layer};
            default -> new int[]{0, 0};
        };
    }

    public void addIsland(BlockPos center, DimensionData data, long currentTick) {
        islands.put(center, new ActiveIsland(data, currentTick));
        setDirty();
        Constants.LOG.info("IslandManager: Created island '{}' at {} (expires in {} min)",
                data.name(), center, data.survivalTime());
    }

    public void removeIsland(BlockPos center) {
        if (islands.remove(center) != null) {
            setDirty();
            Constants.LOG.info("IslandManager: Removed expired island at {}", center);
        }
    }

    public ActiveIsland getIsland(BlockPos center) {
        return islands.get(center);
    }

    public Map.Entry<BlockPos, ActiveIsland> findIslandAt(int x, int z) {
        for (Map.Entry<BlockPos, ActiveIsland> entry : islands.entrySet()) {
            BlockPos center = entry.getKey();
            DimensionData data = entry.getValue().data();

            int baseRadius = 400 + (data.difficulty() * 10);
            int maxRadius = baseRadius + 100;

            double dx = (double) center.getX() - x;
            double dz = (double) center.getZ() - z;
            double distSq = dx * dx + dz * dz;
            if (distSq <= (double) maxRadius * maxRadius) {
                return entry;
            }
        }
        return null;
    }

    public List<Map.Entry<BlockPos, ActiveIsland>> getExpiredIslands(long currentTick) {
        List<Map.Entry<BlockPos, ActiveIsland>> expired = new ArrayList<>();
        for (Map.Entry<BlockPos, ActiveIsland> entry : islands.entrySet()) {
            if (entry.getValue().isExpired(currentTick)) {
                expired.add(entry);
            }
        }
        return expired;
    }

    public List<Map.Entry<BlockPos, ActiveIsland>> getDecayingIslands(long currentTick) {
        List<Map.Entry<BlockPos, ActiveIsland>> decaying = new ArrayList<>();
        for (Map.Entry<BlockPos, ActiveIsland> entry : islands.entrySet()) {
            double progress = entry.getValue().decayProgress(currentTick);
            if (progress > 0.0 && progress < 1.0) {
                decaying.add(entry);
            }
        }
        return decaying;
    }

    public Map<BlockPos, ActiveIsland> getAllIslands() {
        return Map.copyOf(islands);
    }

    public static IslandManager get(ServerLevel level) {
        SavedDataType<IslandManager> type = new SavedDataType<>(
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, "islands"),
                IslandManager::new,
                CODEC,
                null
        );
        IslandManager manager = level.getDataStorage().computeIfAbsent(type);
        
        // Cache the instance for the void level so ChunkGenerator can access it
        if (level.dimension() == ModDimensions.VOID_LEVEL) {
            voidInstance.set(manager);
        }
        
        return manager;
    }

    public static IslandManager getVoidInstance() {
        return voidInstance.get();
    }
}

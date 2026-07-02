package tnpl.fractureddimensions.worldgen.island;

import java.util.HashMap;
import java.util.Map;

public class IslandGeneratorRegistry {
    private static final Map<String, IslandTypeGenerator> GENERATORS = new HashMap<>();

    private static final IslandTypeGenerator FALLBACK = new DefaultPlanetGenerator(); 

    static {
        register(0, 0, new YellowDwarfGenerator());
        register(1, 0, new PurplePlanetGenerator());
        register(2, 0, new AsteroidGenerator());
    }

    public static void register(int type, int variant, IslandTypeGenerator generator) {
        GENERATORS.put(type + "_" + variant, generator);
    }

    public static IslandTypeGenerator getGenerator(int type, int variant) {
        return GENERATORS.getOrDefault(type + "_" + variant, FALLBACK);
    }
}

package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import tnpl.fractureddimensions.Constants;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> METEORIC_FUELS = create("meteoric_fuels");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}

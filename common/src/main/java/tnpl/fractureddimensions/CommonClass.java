package tnpl.fractureddimensions;

import tnpl.fractureddimensions.registry.ModBlockEntities;
import tnpl.fractureddimensions.registry.ModBlocks;
import tnpl.fractureddimensions.registry.ModCreativeTabs;
import tnpl.fractureddimensions.registry.ModItems;
import tnpl.fractureddimensions.registry.ModDataComponents;
import tnpl.fractureddimensions.registry.ModMenus;

public class CommonClass {

    public static void init() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModCreativeTabs.init();
        ModDataComponents.init();
        ModMenus.init();
    }
}
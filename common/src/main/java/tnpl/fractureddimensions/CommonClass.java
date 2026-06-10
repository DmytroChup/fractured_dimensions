package tnpl.fractureddimensions;

import tnpl.fractureddimensions.registry.*;

public class CommonClass {

    public static void init() {
        ModBlocks.init();
        ModItems.init();
        ModBlockEntities.init();
        ModCreativeTabs.init();
        ModDataComponents.init();
        ModMenus.init();
        ModDimensions.init();
    }
}
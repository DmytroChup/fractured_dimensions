package tnpl.fractureddimensions.platform;


import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.platform.services.IEnergyPlatformHelper;
import tnpl.fractureddimensions.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static final IEnergyPlatformHelper ENERGY = load(IEnergyPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
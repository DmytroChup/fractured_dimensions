package tnpl.fractureddimensions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import tnpl.fractureddimensions.Constants;
import tnpl.fractureddimensions.registration.RegistrationProvider;
import tnpl.fractureddimensions.registration.RegistryObject;

public class ModSounds {
    public static final RegistrationProvider<SoundEvent> SOUNDS = RegistrationProvider.get(BuiltInRegistries.SOUND_EVENT, Constants.MOD_ID);

    public static final RegistryObject<SoundEvent, SoundEvent> PHOTOSPHERIQUE_SHOOT =
            SOUNDS.register("photospherique_shoot",
            () -> SoundEvent.createVariableRangeEvent(
                    Identifier.fromNamespaceAndPath(Constants.MOD_ID, "photospherique_shoot")
            ));

    public static final RegistryObject<SoundEvent, SoundEvent> PHOTOSPHERIQUE_CHARGE =
            SOUNDS.register("photospherique_charge",
            () -> SoundEvent.createVariableRangeEvent(
                    Identifier.fromNamespaceAndPath(Constants.MOD_ID, "photospherique_charge")
            ));

    public static final RegistryObject<SoundEvent, SoundEvent> PHOTOSPHERIQUE_SUPERNOVA =
            SOUNDS.register("photospherique_supernova",
            () -> SoundEvent.createVariableRangeEvent(
                    Identifier.fromNamespaceAndPath(Constants.MOD_ID, "photospherique_supernova")
            ));

    public static void init() {
    }
}

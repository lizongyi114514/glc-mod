package com.greenlantern.corps.registry;

import com.greenlantern.corps.GLCMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GLCMod.MODID);

    public static final RegistryObject<SoundEvent> CAST = register("cast");
    public static final RegistryObject<SoundEvent> SUMMON = register("summon");
    public static final RegistryObject<SoundEvent> HIT = register("hit");

    private ModSounds() {
    }

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(GLCMod.MODID, name)));
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}

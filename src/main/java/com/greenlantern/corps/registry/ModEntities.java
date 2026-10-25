package com.greenlantern.corps.registry;

import com.greenlantern.corps.GLCMod;
import com.greenlantern.corps.entity.LanternScoutEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GLCMod.MODID);

    public static final RegistryObject<EntityType<LanternScoutEntity>> LANTERN_SCOUT =
            ENTITIES.register("lantern_scout", () -> EntityType.Builder
                    .of(LanternScoutEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("lantern_scout"));

    private ModEntities() {
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(LANTERN_SCOUT.get(), LanternScoutEntity.createAttributes().build());
    }
}

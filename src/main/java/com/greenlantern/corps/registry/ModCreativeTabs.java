package com.greenlantern.corps.registry;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.GLCMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GLCMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.glc"))
                    .icon(() -> new ItemStack(ModItems.ring(CorpsColor.GREEN)))
                    .displayItems((params, output) -> {
                        for (CorpsColor color : CorpsColor.values()) {
                            output.accept(ModItems.ring(color));
                        }
                        output.accept(ModBlocks.BATTERY_ITEM.get());
                        ModItems.EGGS.values().forEach(egg -> output.accept(egg.get()));
                    })
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}

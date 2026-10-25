package com.greenlantern.corps;

import com.greenlantern.corps.network.Network;
import com.greenlantern.corps.registry.ModBlocks;
import com.greenlantern.corps.registry.ModCreativeTabs;
import com.greenlantern.corps.registry.ModEntities;
import com.greenlantern.corps.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;

@Mod(GLCMod.MODID)
public class GLCMod {
    public static final String MODID = "glc";

    public GLCMod(IEventBus modBus, ModContainer container) {
        ModItems.register(modBus);
        ModBlocks.register(modBus);
        ModEntities.register(modBus);
        modBus.addListener(ModEntities::onAttributes);
        ModCreativeTabs.register(modBus);
        Network.register();
    }
}

package com.greenlantern.corps;

import com.greenlantern.corps.network.Network;
import com.greenlantern.corps.registry.ModBlocks;
import com.greenlantern.corps.registry.ModCreativeTabs;
import com.greenlantern.corps.registry.ModEntities;
import com.greenlantern.corps.registry.ModItems;
import com.greenlantern.corps.registry.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GLCMod.MODID)
public class GLCMod {
    public static final String MODID = "glc";

    /** Forge 需要无参构造函数。 */
    public GLCMod() {
        this(FMLJavaModLoadingContext.get().getModEventBus(), null);
    }

    public GLCMod(IEventBus modBus, ModContainer container) {
        ModItems.register(modBus);
        ModBlocks.register(modBus);
        ModEntities.register(modBus);
        ModSounds.register(modBus);
        modBus.addListener(ModEntities::onAttributes);
        ModCreativeTabs.register(modBus);
        Network.register();
    }
}

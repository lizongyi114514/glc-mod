package com.greenlantern.corps.registry;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.GLCMod;
import com.greenlantern.corps.item.LanternEggItem;
import com.greenlantern.corps.item.RingItem;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GLCMod.MODID);

    public static final Map<CorpsColor, RegistryObject<Item>> RINGS = new EnumMap<>(CorpsColor.class);
    public static final Map<String, RegistryObject<Item>> EGGS = new LinkedHashMap<>();

    static {
        for (CorpsColor color : CorpsColor.values()) {
            RINGS.put(color, ITEMS.register("ring_" + color.id(),
                    () -> new RingItem(color, new Item.Properties().stacksTo(1).rarity(Rarity.RARE))));
        }
        egg("hal", "hal", "哈尔·乔丹", "glc_duel_hal", ChatFormatting.GREEN);
        egg("john", "john", "约翰·斯图尔特", "glc_duel_john", ChatFormatting.GREEN);
        egg("kyle", "kyle", "凯尔·雷纳", "glc_duel_kyle", ChatFormatting.GREEN);
        egg("guy", "guy", "盖·加德纳", "glc_duel_guy", ChatFormatting.GREEN);
        egg("sinestro", "yellow", "塞尼斯托", "glc_duel_sinestro", ChatFormatting.YELLOW);
        egg("atro", "red", "阿托希塔斯", "glc_duel_atro", ChatFormatting.RED);
        egg("walker", "blue", "圣行者", "glc_duel_walker", ChatFormatting.AQUA);
        egg("larfleeze", "orange", "拉弗利兹", "glc_duel_larfleeze", ChatFormatting.GOLD);
        egg("trooper", "trooper", "绿灯巡逻兵", "glc_trooper", ChatFormatting.GREEN);
    }

    public static final RegistryObject<Item> SCOUT_SPAWN_EGG = EGGS.get("trooper");

    private ModItems() {
    }

    private static void egg(String id, String entity, String name, String tag, ChatFormatting color) {
        EGGS.put(id, ITEMS.register(id + "_spawn_egg",
                () -> new LanternEggItem(new Item.Properties().stacksTo(16), entity, name, tag, color)));
    }

    public static Item ring(CorpsColor color) {
        return RINGS.get(color).get();
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}

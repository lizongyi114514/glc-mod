package com.greenlantern.corps.registry;

import com.greenlantern.corps.GLCMod;
import com.greenlantern.corps.block.BatteryBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GLCMod.MODID);

    public static final RegistryObject<Block> BATTERY = BLOCKS.register("battery",
            () -> new BatteryBlock(BlockBehaviour.Properties.of()
                    .strength(5.0F, 6.0F)
                    .lightLevel(state -> 15)
                    .sound(SoundType.GLASS)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Item> BATTERY_ITEM = ModItems.ITEMS.register("battery",
            () -> new BlockItem(BATTERY.get(), new Item.Properties()));

    private ModBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}

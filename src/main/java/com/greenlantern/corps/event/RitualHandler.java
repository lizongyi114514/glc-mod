package com.greenlantern.corps.event;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.block.BatteryBlock;
import com.greenlantern.corps.item.RingItem;
import com.greenlantern.corps.network.Network;
import com.greenlantern.corps.util.GLCUtil;
import com.greenlantern.corps.util.Will;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "glc")
public final class RitualHandler {
    private static final String[] OATH = {
            "白昼朗朗，黑夜茫茫；",
            "魑魅魍魉，无所遁藏。",
            "邪徒奸党，惧吾神光；",
            "绿灯长明，万世光芒！"
    };

    private static final Map<UUID, Ritual> ACTIVE = new HashMap<>();

    private RitualHandler() {
    }

    private static final class Ritual {
        private final BlockPos battery;
        private int ticks;
        private int step;

        private Ritual(BlockPos battery) {
            this.battery = battery;
            this.ticks = 0;
            this.step = 0;
        }
    }

    public static void start(ServerPlayer player, ItemStack ring, BlockPos battery) {
        if (ACTIVE.containsKey(player.getUUID())) {
            GLCUtil.msg(player, "仪式已经在进行。", ChatFormatting.GRAY, true);
            return;
        }
        if (!(ring.getItem() instanceof RingItem)) {
            return;
        }
        ACTIVE.put(player.getUUID(), new Ritual(battery.immutable()));
        player.sendSystemMessage(Component.literal("[绿灯军团] ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("充能仪式开始：留在电池旁，复诵誓词。").withStyle(ChatFormatting.GRAY)));
        GLCUtil.sound(player.level(), player.position(), SoundEvents.BEACON_ACTIVATE, 1.0F, 1.0F);
        sayLine(player, 0);
    }

    private static void sayLine(ServerPlayer player, int index) {
        player.sendSystemMessage(Component.literal("「" + OATH[index] + "」").withStyle(ChatFormatting.GREEN));
        GLCUtil.dust(player.serverLevel(), player.position().add(0, 1, 0), CorpsColor.GREEN, 30, 0.6);
        GLCUtil.sound(player.level(), player.position(), SoundEvents.AMETHYST_BLOCK_CHIME, 0.6F, 1.2F);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ACTIVE.isEmpty()) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        Iterator<Map.Entry<UUID, Ritual>> iterator = ACTIVE.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Ritual> entry = iterator.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }
            Ritual ritual = entry.getValue();
            if (!(player.level().getBlockState(ritual.battery).getBlock() instanceof BatteryBlock)
                    || player.blockPosition().distSqr(ritual.battery) > 100.0D) {
                iterator.remove();
                GLCUtil.msg(player, "誓词中断：你离开了电池。", ChatFormatting.RED, true);
                GLCUtil.sound(player.level(), player.position(), SoundEvents.BEACON_DEACTIVATE, 0.6F, 0.8F);
                continue;
            }
            ritual.ticks++;
            if (ritual.ticks % 40 == 0) {
                ritual.step++;
                if (ritual.step < OATH.length) {
                    sayLine(player, ritual.step);
                } else {
                    iterator.remove();
                    finish(player);
                }
            }
        }
    }

    private static void finish(ServerPlayer player) {
        ItemStack held = GLCUtil.heldRing(player);
        if (!held.isEmpty()) {
            RingItem.setCharge(held, RingItem.MAX_CHARGE);
        }
        Will.set(player, 100);
        Network.syncWill(player);
        player.sendSystemMessage(Component.literal("[绿灯军团] ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("誓词已记录在案。戒指重新亮起。").withStyle(ChatFormatting.GRAY)));
        player.displayClientMessage(Component.literal("绿灯长明 · 充能 100%").withStyle(ChatFormatting.GREEN), false);
        GLCUtil.sound(player.level(), player.position(), SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
        GLCUtil.dust(player.serverLevel(), player.position().add(0, 1, 0), CorpsColor.GREEN, 120, 1.4);
        GLCUtil.award(player, "oath");
    }
}

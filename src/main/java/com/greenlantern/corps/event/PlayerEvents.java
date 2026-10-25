package com.greenlantern.corps.event;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.block.BatteryBlock;
import com.greenlantern.corps.item.RingItem;
import com.greenlantern.corps.network.Network;
import com.greenlantern.corps.util.GLCUtil;
import com.greenlantern.corps.util.Will;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = "glc")
public final class PlayerEvents {
    private PlayerEvents() {
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Will.init(player);
            Network.syncWill(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        int will = Will.get(event.getOriginal());
        if (event.isWasDeath()) {
            will = Math.max(0, will - 25);
        }
        Will.set(player, will);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer killer
                && !GLCUtil.heldRing(killer).isEmpty()
                && event.getEntity() != killer) {
            Will.add(killer, 4);
            Network.syncWill(killer);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (GLCUtil.heldRing(player).isEmpty()) {
            return;
        }
        float remaining = player.getHealth() - event.getAmount();
        if (remaining > 8.0F) {
            return;
        }
        long now = player.level().getGameTime();
        long cooldown = player.getPersistentData().getLong("glc_hurt_cd");
        if (now < cooldown) {
            return;
        }
        player.getPersistentData().putLong("glc_hurt_cd", now + 200L);
        Will.add(player, 8);
        Network.syncWill(player);
        GLCUtil.msg(player, "恐惧之后，意志仍在。", ChatFormatting.GREEN, true);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }
        if (player.tickCount % 20 != 0) {
            return;
        }
        tickSecond(player);
    }

    private static void tickSecond(ServerPlayer player) {
        ItemStack ring = GLCUtil.heldRing(player);
        if (ring.isEmpty()) {
            return;
        }
        Will.add(player, 1);
        CorpsPassives.apply(player, RingItem.corpsOf(ring));
        if (BatteryBlock.hasBatteryNear(player.level(), player.blockPosition(), 8)) {
            RingItem.setCharge(ring, RingItem.getCharge(ring) + 1);
        }
        Network.syncWill(player);
    }

    public static List<ServerPlayer> nearbyPlayers(ServerPlayer player, double radius) {
        return player.serverLevel().players().stream()
                .filter(other -> other.distanceToSqr(player) <= radius * radius)
                .toList();
    }

    public static void applyNearby(ServerPlayer player, double radius, java.util.function.Consumer<LivingEntity> action) {
        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius))) {
            action.accept(entity);
        }
    }

    public static void applyToMobs(ServerPlayer player, double radius, java.util.function.Consumer<Mob> action) {
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(radius))) {
            action.accept(mob);
        }
    }

    public static MobEffectInstance effect(net.minecraft.world.effect.MobEffect effect, int seconds, int amplifier) {
        return new MobEffectInstance(effect, seconds * 20, amplifier);
    }
}

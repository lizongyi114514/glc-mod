package com.greenlantern.corps.event;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.item.RingItem;
import com.greenlantern.corps.util.GLCUtil;
import com.greenlantern.corps.util.Will;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

public final class CorpsPassives {
    private CorpsPassives() {
    }

    public static void apply(ServerPlayer player, CorpsColor corps) {
        switch (corps) {
            case GREEN -> Will.add(player, 1);
            case YELLOW -> PlayerEvents.applyToMobs(player, 8.0D, mob ->
                    mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0)));
            case RED -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0));
                Will.add(player, -2);
                if (Will.get(player) <= 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 1));
                    GLCUtil.msg(player, "愤怒接管了戒指。", ChatFormatting.RED, true);
                }
            }
            case BLUE -> {
                for (ServerPlayer other : PlayerEvents.nearbyPlayers(player, 8.0D)) {
                    other.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0));
                }
            }
            case INDIGO -> {
                boolean allyHurt = PlayerEvents.nearbyPlayers(player, 16.0D).stream()
                        .anyMatch(other -> other.getHealth() <= 7.0F);
                player.addEffect(new MobEffectInstance(allyHurt ? MobEffects.DAMAGE_BOOST : MobEffects.WEAKNESS, 40, 0));
            }
            case VIOLET -> {
                PlayerEvents.applyToMobs(player, 8.0D, mob ->
                        mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1)));
                for (ServerPlayer other : PlayerEvents.nearbyPlayers(player, 16.0D)) {
                    if (other.getHealth() <= 10.0F) {
                        other.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 60, 0));
                    }
                }
            }
            case ORANGE -> {
                player.addEffect(new MobEffectInstance(MobEffects.LUCK, 60, 0));
                int orange = 0;
                for (ServerPlayer other : player.serverLevel().players()) {
                    ItemStack held = other.getMainHandItem();
                    if (RingItem.isRing(held) && RingItem.corpsOf(held) == CorpsColor.ORANGE) {
                        orange++;
                    }
                }
                if (orange > 1) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
                }
            }
        }
    }
}

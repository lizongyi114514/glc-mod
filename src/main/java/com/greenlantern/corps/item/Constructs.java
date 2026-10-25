package com.greenlantern.corps.item;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.network.Network;
import com.greenlantern.corps.util.GLCUtil;
import com.greenlantern.corps.util.Will;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public final class Constructs {
    private Constructs() {
    }

    public static void cast(ServerPlayer player, ItemStack ring, CorpsColor corps) {
        Construct construct = Construct.byId(RingItem.getMode(ring));
        if (construct == Construct.NONE) {
            Network.openMenu(player);
            return;
        }
        if (player.getCooldowns().isOnCooldown(ring.getItem())) {
            GLCUtil.msg(player, "构念冷却中。", ChatFormatting.GRAY, true);
            GLCUtil.sound(player.level(), player.position(), SoundEvents.NOTE_BLOCK_BASS.get(), 0.4F, 0.8F);
            return;
        }
        if (Will.get(player) < construct.will()) {
            GLCUtil.msg(player, "意志不足。戒指不会回应一个动摇的人。", ChatFormatting.RED, true);
            GLCUtil.sound(player.level(), player.position(), SoundEvents.NOTE_BLOCK_BASS.get(), 0.5F, 0.6F);
            return;
        }
        if (RingItem.getCharge(ring) < construct.charge()) {
            GLCUtil.msg(player, "充能耗尽。回电池旁复诵誓词。", ChatFormatting.RED, true);
            GLCUtil.sound(player.level(), player.position(), SoundEvents.BEACON_DEACTIVATE, 0.5F, 0.7F);
            return;
        }
        if (construct == Construct.BLADE && corps == CorpsColor.BLUE && !greenAllyNearby(player)) {
            GLCUtil.msg(player, "蓝光不孤立存在：附近需要一位绿灯持有者。", ChatFormatting.AQUA, true);
            return;
        }

        RingItem.setCharge(ring, RingItem.getCharge(ring) - construct.charge());
        Will.add(player, -construct.will());
        player.getCooldowns().addCooldown(ring.getItem(), construct.cooldown());
        Network.syncWill(player);

        apply(player, corps, construct);
        GLCUtil.award(player, "first_construct");
    }

    private static void apply(ServerPlayer player, CorpsColor corps, Construct construct) {
        ServerLevel level = player.serverLevel();
        Vec3 pos = player.position().add(0, 1, 0);

        switch (construct) {
            case FIST -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 60, 0));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0));
                GLCUtil.line(level, player.getEyePosition(),
                        player.getEyePosition().add(player.getLookAngle().scale(2.6D)), corps, 10);
                GLCUtil.dust(level, pos, corps, 18, 0.4);
                GLCUtil.sound(level, player.position(), SoundEvents.PLAYER_ATTACK_SWEEP, 0.8F, 1.2F);
                GLCUtil.msg(player, "构念 · 巨拳", ChatFormatting.GREEN, true);
            }
            case SHIELD -> {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 160, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 160, 3));
                GLCUtil.sphere(level, player.position().add(0, 1.0D, 0), 1.25D, corps, 4);
                GLCUtil.sound(level, player.position(), SoundEvents.CONDUIT_ACTIVATE, 0.9F, 1.1F);
                GLCUtil.msg(player, "构念 · 护盾", ChatFormatting.GREEN, true);
            }
            case BLADE -> {
                Vec3 look = player.getLookAngle();
                AABB area = player.getBoundingBox().inflate(5.0D).move(look.x * 2.0D, look.y * 2.0D, look.z * 2.0D);
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                        entity -> entity != player && entity.isAlive() && !(entity instanceof Player));
                for (LivingEntity target : targets) {
                    target.hurt(player.damageSources().playerAttack(player), 7.0F);
                    GLCUtil.dust(level, target.position().add(0, 1, 0), corps, 12, 0.3);
                }
                Vec3 eye = player.getEyePosition();
                for (int step = -6; step <= 6; step++) {
                    double angle = Math.toRadians(step * 11.0D);
                    double cos = Math.cos(angle);
                    double sin = Math.sin(angle);
                    Vec3 direction = new Vec3(look.x * cos - look.z * sin, look.y, look.x * sin + look.z * cos);
                    GLCUtil.dust(level, eye.add(direction.scale(3.2D)), corps, 2, 0.05);
                }
                GLCUtil.sound(level, player.position(), SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.9F);
                GLCUtil.msg(player, "构念 · 刃", ChatFormatting.GREEN, true);
            }
            case WINGS -> {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 280, 0));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 1));
                for (int side = -1; side <= 1; side += 2) {
                    for (int i = 0; i < 8; i++) {
                        double t = i / 7.0D;
                        Vec3 point = player.position().add(side * (0.35D + t * 0.95D), 1.35D + t * 0.55D, -0.25D - t * 0.45D);
                        GLCUtil.dust(level, point, corps, 2, 0.05);
                    }
                }
                GLCUtil.sound(level, player.position(), SoundEvents.TRIDENT_THROW, 0.7F, 1.3F);
                GLCUtil.msg(player, "构念 · 翼", ChatFormatting.GREEN, true);
            }
            case GRAB -> {
                LivingEntity target = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(12.0D),
                                entity -> entity != player && entity.isAlive() && !(entity instanceof Player))
                        .stream()
                        .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)))
                        .orElse(null);
                if (target != null) {
                    Vec3 direction = player.position().subtract(target.position()).normalize();
                    target.setDeltaMovement(direction.x * 0.9D, 0.35D, direction.z * 0.9D);
                    target.hurtMarked = true;
                    GLCUtil.line(level, player.getEyePosition(), target.position().add(0, 1, 0), corps, 16);
                    GLCUtil.dust(level, target.position().add(0, 1, 0), corps, 15, 0.3);
                }
                GLCUtil.sound(level, player.position(), SoundEvents.ENDER_PEARL_THROW, 0.8F, 0.7F);
                GLCUtil.msg(player, "构念 · 抓取", ChatFormatting.GREEN, true);
            }
            case SCAN -> {
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                        player.getBoundingBox().inflate(32.0D), entity -> entity != player && entity.isAlive());
                for (LivingEntity target : targets) {
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
                }
                GLCUtil.sphere(level, player.position().add(0, 1.0D, 0), 2.0D, corps, 3);
                GLCUtil.sound(level, player.position(), SoundEvents.BEACON_ACTIVATE, 0.8F, 1.6F);
                GLCUtil.msg(player, "构念 · 扫描：标记 " + targets.size() + " 个目标", ChatFormatting.GREEN, true);
            }
            case CLOAK -> {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0));
                GLCUtil.dust(level, pos, corps, 24, 0.5);
                GLCUtil.sound(level, player.position(), SoundEvents.AMETHYST_BLOCK_CHIME, 0.9F, 1.4F);
                GLCUtil.msg(player, "构念 · 隐身", ChatFormatting.GREEN, true);
            }
            case CHAIN -> {
                LivingEntity target = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(12.0D),
                                entity -> entity != player && entity.isAlive() && !(entity instanceof Player))
                        .stream()
                        .min(Comparator.comparingDouble(entity -> entity.distanceToSqr(player)))
                        .orElse(null);
                if (target != null) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3));
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0));
                    GLCUtil.line(level, player.getEyePosition(), target.position().add(0, 1, 0), corps, 24);
                    GLCUtil.dust(level, target.position().add(0, 1, 0), corps, 20, 0.35);
                }
                GLCUtil.sound(level, player.position(), SoundEvents.CHAIN_PLACE, 1.0F, 0.8F);
                GLCUtil.msg(player, "构念 · 锁链", ChatFormatting.GREEN, true);
            }
            case GATLING -> {
                for (int volley = 0; volley < 3; volley++) {
                    List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                            player.getBoundingBox().inflate(8.0D), entity -> entity != player && entity.isAlive() && !(entity instanceof Player));
                    for (LivingEntity target : targets) {
                        target.hurt(player.damageSources().playerAttack(player), 2.0F);
                    }
                    GLCUtil.dust(level, player.getEyePosition().add(player.getLookAngle().scale(3.0D)), corps, 12, 0.3);
                }
                GLCUtil.sound(level, player.position(), SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 1.4F);
                GLCUtil.msg(player, "构念 · 加特林", ChatFormatting.GREEN, true);
            }
            case FLIGHT -> {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
                if (player.getServer() != null) {
                    player.getServer().tell(new net.minecraft.server.TickTask(400, () -> {
                        if (!player.isCreative() && !player.isSpectator()) {
                            player.getAbilities().mayfly = false;
                            player.getAbilities().flying = false;
                            player.onUpdateAbilities();
                        }
                    }));
                }
                GLCUtil.sphere(level, player.position().add(0, 1.0D, 0), 1.4D, corps, 4);
                GLCUtil.sound(level, player.position(), SoundEvents.ELYTRA_FLYING, 0.9F, 1.2F);
                GLCUtil.msg(player, "构念 · 飞行：20 秒", ChatFormatting.GREEN, true);
            }
            default -> {
            }
        }
    }

    public static boolean greenAllyNearby(ServerPlayer player) {
        for (ServerPlayer other : player.serverLevel().players()) {
            if (other == player) {
                continue;
            }
            if (other.distanceToSqr(player) > 256.0D) {
                continue;
            }
            ItemStack held = other.getMainHandItem();
            if (RingItem.isRing(held) && RingItem.corpsOf(held) == CorpsColor.GREEN) {
                return true;
            }
        }
        return false;
    }
}

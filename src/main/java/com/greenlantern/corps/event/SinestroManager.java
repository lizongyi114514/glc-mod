package com.greenlantern.corps.event;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.item.Construct;
import com.greenlantern.corps.item.RingItem;
import com.greenlantern.corps.registry.ModItems;
import com.greenlantern.corps.util.GLCUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = "glc")
public final class SinestroManager {
    private static final int LEATHER_YELLOW = 0xFFE23C;

    private static UUID bossId;
    private static ServerLevel bossLevel;
    private static ServerBossEvent bossBar;
    private static int abilityTimer;
    private static boolean phaseTwo;

    private SinestroManager() {
    }

    public static void spawn(ServerPlayer player) {
        if (bossId != null && bossLevel != null && bossLevel.getEntity(bossId) != null) {
            GLCUtil.msg(player, "塞尼斯托已经在场。", ChatFormatting.GRAY, false);
            return;
        }
        ServerLevel level = player.serverLevel();
        Vindicator boss = EntityType.VINDICATOR.create(level);
        if (boss == null) {
            return;
        }
        boss.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), 0.0F);
        boss.setCustomName(Component.literal("塔尔·塞尼斯托").withStyle(ChatFormatting.YELLOW));
        boss.setCustomNameVisible(true);
        boss.setPersistenceRequired();
        if (boss.getAttribute(Attributes.MAX_HEALTH) != null) {
            boss.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200.0D);
        }
        boss.setHealth(200.0F);
        boss.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        boss.setItemSlot(EquipmentSlot.HEAD, dyed(new ItemStack(Items.LEATHER_HELMET)));
        boss.setItemSlot(EquipmentSlot.CHEST, dyed(new ItemStack(Items.LEATHER_CHESTPLATE)));
        boss.setItemSlot(EquipmentSlot.LEGS, dyed(new ItemStack(Items.LEATHER_LEGGINGS)));
        boss.setItemSlot(EquipmentSlot.FEET, dyed(new ItemStack(Items.LEATHER_BOOTS)));
        boss.getTags().add("glc_boss");
        level.addFreshEntity(boss);

        bossId = boss.getUUID();
        bossLevel = level;
        abilityTimer = 0;
        phaseTwo = false;
        bossBar = new ServerBossEvent(Component.literal("塔尔·塞尼斯托"),
                BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);
        bossBar.setProgress(1.0F);
        bossBar.setVisible(true);
        for (ServerPlayer other : level.players()) {
            bossBar.addPlayer(other);
            other.sendSystemMessage(Component.literal("[塞尼斯托] ").withStyle(ChatFormatting.YELLOW)
                    .append(Component.literal("你们以为意志能赢过恐惧？").withStyle(ChatFormatting.GRAY)));
        }
        level.playSound(null, player.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON,
                SoundSource.HOSTILE, 1.0F, 0.6F);
    }

    private static ItemStack dyed(ItemStack stack) {
        if (stack.getItem() instanceof DyeableLeatherItem dyeable) {
            dyeable.setColor(stack, LEATHER_YELLOW);
        }
        return stack;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || bossId == null || bossLevel == null) {
            return;
        }
        Entity entity = bossLevel.getEntity(bossId);
        if (!(entity instanceof LivingEntity boss) || !boss.isAlive()) {
            clearBossBar();
            return;
        }
        if (bossBar != null) {
            bossBar.setProgress(Mth.clamp(boss.getHealth() / boss.getMaxHealth(), 0.0F, 1.0F));
            for (ServerPlayer other : bossLevel.players()) {
                bossBar.addPlayer(other);
            }
        }
        abilityTimer++;
        if (abilityTimer % 10 == 0) {
            GLCUtil.ring(bossLevel, boss.position(), 1.25D, 1.0D, CorpsColor.YELLOW, 18);
        }
        if (abilityTimer % 100 == 0) {
            cast(level(), boss);
        }
        if (!phaseTwo && boss.getHealth() <= 100.0F) {
            phaseTwo = true;
            summonAdds(level(), boss);
            for (ServerPlayer other : bossLevel.players()) {
                other.sendSystemMessage(Component.literal("[塞尼斯托] ").withStyle(ChatFormatting.YELLOW)
                        .append(Component.literal("恐惧不需要理由，只需要一个缺口。").withStyle(ChatFormatting.GRAY)));
                other.sendSystemMessage(Component.literal("[绿灯军团] ").withStyle(ChatFormatting.GREEN)
                        .append(Component.literal("黄灯新兵正在集结。").withStyle(ChatFormatting.GRAY)));
            }
        }
    }

    private static ServerLevel level() {
        return bossLevel;
    }

    private static void cast(ServerLevel level, LivingEntity boss) {
        ServerPlayer target = level.getNearestPlayer(boss, 32.0D);
        if (target == null) {
            return;
        }
        EvokerFangs fangs = EntityType.EVOKER_FANGS.create(level);
        if (fangs != null) {
            fangs.moveTo(target.getX(), target.getY(), target.getZ(), 0.0F, 0.0F);
            fangs.setOwner(boss);
            level.addFreshEntity(fangs);
        }
        level.playSound(null, target.blockPosition(), SoundEvents.EVOKER_CAST_SPELL,
                SoundSource.HOSTILE, 1.0F, 0.8F);
    }

    private static void summonAdds(ServerLevel level, LivingEntity boss) {
        for (int i = 0; i < 2; i++) {
            Pillager pillager = EntityType.PILLAGER.create(level);
            if (pillager == null) {
                continue;
            }
            double offset = i == 0 ? 2.0D : -2.0D;
            pillager.moveTo(boss.getX() + offset, boss.getY(), boss.getZ(), 0.0F, 0.0F);
            pillager.setCustomName(Component.literal("黄灯新兵").withStyle(ChatFormatting.YELLOW));
            pillager.setCustomNameVisible(true);
            pillager.setPersistenceRequired();
            pillager.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
            level.addFreshEntity(pillager);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!event.getEntity().getTags().contains("glc_boss")) {
            return;
        }
        Vec3 pos = event.getEntity().position();
        ServerLevel level = (ServerLevel) event.getEntity().level();
        clearBossBar();
        bossId = null;
        phaseTwo = false;
        for (ServerPlayer other : level.players()) {
            if (other.distanceToSqr(pos) <= 64.0D * 64.0D) {
                other.getInventory().add(new ItemStack(ModItems.ring(CorpsColor.YELLOW)));
                GLCUtil.award(other, "sinestro");
                other.sendSystemMessage(Component.literal("[绿灯军团] ").withStyle(ChatFormatting.GREEN)
                        .append(Component.literal("塞尼斯托已被押送回欧阿。黄灯戒指归你了。").withStyle(ChatFormatting.GRAY)));
            }
        }
    }

    private static void clearBossBar() {
        if (bossBar != null) {
            bossBar.removeAllPlayers();
            bossBar.setVisible(false);
            bossBar = null;
        }
    }
}

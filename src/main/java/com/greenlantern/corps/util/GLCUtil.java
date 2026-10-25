package com.greenlantern.corps.util;

import com.greenlantern.corps.CorpsColor;
import com.greenlantern.corps.GLCMod;
import com.greenlantern.corps.item.RingItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class GLCUtil {
    private GLCUtil() {
    }

    public static void msg(Player player, String text, ChatFormatting color, boolean actionBar) {
        player.displayClientMessage(Component.literal(text).withStyle(color), actionBar);
    }

    public static void dust(ServerLevel level, Vec3 pos, CorpsColor color, int count, double spread) {
        level.sendParticles(
                new DustParticleOptions(new Vector3f(color.red(), color.green(), color.blue()), 1.2F),
                pos.x, pos.y, pos.z, count, spread, spread, spread, 0.0);
    }

    public static void sound(Level level, Vec3 pos, SoundEvent event, float volume, float pitch) {
        level.playSound(null, pos.x, pos.y, pos.z, event, SoundSource.PLAYERS, volume, pitch);
    }

    public static void ring(ServerLevel level, Vec3 center, double radius, double y, CorpsColor color, int points) {
        Vector3f rgb = new Vector3f(color.red(), color.green(), color.blue());
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0 * i / points;
            level.sendParticles(new DustParticleOptions(rgb, 1.0F),
                    center.x + Math.cos(angle) * radius,
                    center.y + y,
                    center.z + Math.sin(angle) * radius,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public static void line(ServerLevel level, Vec3 from, Vec3 to, CorpsColor color, int points) {
        Vector3f rgb = new Vector3f(color.red(), color.green(), color.blue());
        for (int i = 0; i <= points; i++) {
            double t = (double) i / points;
            level.sendParticles(new DustParticleOptions(rgb, 0.9F),
                    from.x + (to.x - from.x) * t,
                    from.y + (to.y - from.y) * t,
                    from.z + (to.z - from.z) * t,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    public static void sphere(ServerLevel level, Vec3 center, double radius, CorpsColor color, int layers) {
        for (int layer = 0; layer < layers; layer++) {
            double y = -radius + 2.0 * radius * layer / Math.max(1, layers - 1);
            double ringRadius = Math.sqrt(Math.max(0.0, radius * radius - y * y));
            ring(level, center, ringRadius, y, color, Math.max(10, (int) (ringRadius * 14)));
        }
    }

    public static ItemStack heldRing(Player player) {
        ItemStack stack = player.getMainHandItem();
        return stack.getItem() instanceof RingItem ? stack : ItemStack.EMPTY;
    }

    public static RingItem heldRingItem(Player player) {
        ItemStack stack = player.getMainHandItem();
        return stack.getItem() instanceof RingItem ring ? ring : null;
    }

    public static void award(ServerPlayer player, String id) {
        var advancement = player.server.getAdvancements()
                .getAdvancement(new ResourceLocation(GLCMod.MODID, id));
        if (advancement != null) {
            player.getAdvancements().award(advancement, "granted");
        }
    }
}

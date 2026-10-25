package com.greenlantern.corps.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class Will {
    private static final String KEY = "glc_will";

    private Will() {
    }

    public static int get(Player player) {
        return player.getPersistentData().getInt(KEY);
    }

    public static void set(Player player, int value) {
        player.getPersistentData().putInt(KEY, Mth.clamp(value, 0, 100));
    }

    public static void add(Player player, int delta) {
        set(player, get(player) + delta);
    }

    public static boolean has(Player player) {
        return player.getPersistentData().contains(KEY);
    }

    public static void init(Player player) {
        if (!has(player)) {
            set(player, 60);
        }
    }
}

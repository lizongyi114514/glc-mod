package com.greenlantern.corps;

import net.minecraft.ChatFormatting;

public enum CorpsColor {
    GREEN("green", "绿灯", 0x3CFF6E, ChatFormatting.GREEN, "minecraft:green_dye"),
    YELLOW("yellow", "黄灯", 0xFFE23C, ChatFormatting.YELLOW, "minecraft:yellow_dye"),
    RED("red", "红灯", 0xFF4B3C, ChatFormatting.RED, "minecraft:red_dye"),
    BLUE("blue", "蓝灯", 0x4BB8FF, ChatFormatting.AQUA, "minecraft:blue_dye"),
    INDIGO("indigo", "青灯", 0x8A6BFF, ChatFormatting.DARK_PURPLE, "minecraft:purple_dye"),
    VIOLET("violet", "紫灯", 0xFF6BD6, ChatFormatting.LIGHT_PURPLE, "minecraft:magenta_dye"),
    ORANGE("orange", "橙灯", 0xFFA23C, ChatFormatting.GOLD, "minecraft:orange_dye");

    private final String id;
    private final String zh;
    private final int rgb;
    private final ChatFormatting formatting;
    private final String dye;

    CorpsColor(String id, String zh, int rgb, ChatFormatting formatting, String dye) {
        this.id = id;
        this.zh = zh;
        this.rgb = rgb;
        this.formatting = formatting;
        this.dye = dye;
    }

    public String id() {
        return id;
    }

    public String zh() {
        return zh;
    }

    public int rgb() {
        return rgb;
    }

    public ChatFormatting formatting() {
        return formatting;
    }

    public String dye() {
        return dye;
    }

    public float red() {
        return ((rgb >> 16) & 0xFF) / 255f;
    }

    public float green() {
        return ((rgb >> 8) & 0xFF) / 255f;
    }

    public float blue() {
        return (rgb & 0xFF) / 255f;
    }

    public static CorpsColor byId(String id) {
        for (CorpsColor c : values()) {
            if (c.id.equals(id)) {
                return c;
            }
        }
        return GREEN;
    }
}

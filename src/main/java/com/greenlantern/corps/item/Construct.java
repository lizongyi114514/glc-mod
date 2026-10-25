package com.greenlantern.corps.item;

public enum Construct {
    NONE(0, "未设定", "先选一个构念", 0, 0, 0),
    FIST(1, "巨拳", "力量 I + 迅捷 I", 3, 5, 30),
    SHIELD(2, "护盾", "抗性 II + 伤害吸收 IV", 5, 8, 160),
    BLADE(3, "刃", "5 格内 7 点伤害", 4, 6, 50),
    WINGS(4, "翼", "缓降 + 迅捷 II + 跳跃 II", 4, 5, 120),
    GRAB(5, "抓取", "拉近 12 格内最近目标", 6, 10, 80);

    private final int id;
    private final String display;
    private final String hint;
    private final int charge;
    private final int will;
    private final int cooldown;

    Construct(int id, String display, String hint, int charge, int will, int cooldown) {
        this.id = id;
        this.display = display;
        this.hint = hint;
        this.charge = charge;
        this.will = will;
        this.cooldown = cooldown;
    }

    public int id() {
        return id;
    }

    public String display() {
        return display;
    }

    public String hint() {
        return hint;
    }

    public int charge() {
        return charge;
    }

    public int will() {
        return will;
    }

    public int cooldown() {
        return cooldown;
    }

    public static Construct byId(int id) {
        for (Construct c : values()) {
            if (c.id == id) {
                return c;
            }
        }
        return NONE;
    }
}

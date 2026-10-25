package com.greenlantern.corps.client;

public final class ClientWill {
    private static int value = 60;

    private ClientWill() {
    }

    public static int get() {
        return value;
    }

    public static void set(int will) {
        value = will;
    }
}

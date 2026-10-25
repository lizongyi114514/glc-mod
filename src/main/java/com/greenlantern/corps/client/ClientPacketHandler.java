package com.greenlantern.corps.client;

import net.minecraft.client.Minecraft;

public final class ClientPacketHandler {
    private ClientPacketHandler() {
    }

    public static void openMenu() {
        Minecraft.getInstance().setScreen(new ConstructScreen());
    }
}

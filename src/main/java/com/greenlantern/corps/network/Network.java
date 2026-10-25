package com.greenlantern.corps.network;

import com.greenlantern.corps.GLCMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class Network {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GLCMod.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private Network() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(SetModePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SetModePacket::encode)
                .decoder(SetModePacket::decode)
                .consumerMainThread(SetModePacket::handle)
                .add();

        CHANNEL.messageBuilder(OpenMenuPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenMenuPacket::encode)
                .decoder(OpenMenuPacket::decode)
                .consumerMainThread(OpenMenuPacket::handle)
                .add();

        CHANNEL.messageBuilder(SyncWillPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncWillPacket::encode)
                .decoder(SyncWillPacket::decode)
                .consumerMainThread(SyncWillPacket::handle)
                .add();
    }

    public static void sendMode(int mode) {
        CHANNEL.sendToServer(new SetModePacket(mode));
    }

    public static void openMenu(net.minecraft.server.level.ServerPlayer player) {
        CHANNEL.send(new OpenMenuPacket(), PacketDistributor.PLAYER.with(() -> player));
    }

    public static void syncWill(net.minecraft.server.level.ServerPlayer player) {
        CHANNEL.send(new SyncWillPacket(com.greenlantern.corps.util.Will.get(player)),
                PacketDistributor.PLAYER.with(() -> player));
    }
}

package com.greenlantern.corps.network;

import com.greenlantern.corps.client.ClientWill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record SyncWillPacket(int will) {
    public static void encode(SyncWillPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.will);
    }

    public static SyncWillPacket decode(FriendlyByteBuf buffer) {
        return new SyncWillPacket(buffer.readVarInt());
    }

    public static void handle(SyncWillPacket packet, NetworkEvent.Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientWill.set(packet.will)));
        context.setPacketHandled(true);
    }
}

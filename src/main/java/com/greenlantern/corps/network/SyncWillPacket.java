package com.greenlantern.corps.network;

import com.greenlantern.corps.client.ClientWill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SyncWillPacket(int will) {
    public static void encode(SyncWillPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.will);
    }

    public static SyncWillPacket decode(FriendlyByteBuf buffer) {
        return new SyncWillPacket(buffer.readVarInt());
    }

    public static void handle(SyncWillPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientWill.set(packet.will)));
        context.setPacketHandled(true);
    }
}

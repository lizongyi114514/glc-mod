package com.greenlantern.corps.network;

import com.greenlantern.corps.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record OpenMenuPacket() {
    public static void encode(OpenMenuPacket packet, FriendlyByteBuf buffer) {
    }

    public static OpenMenuPacket decode(FriendlyByteBuf buffer) {
        return new OpenMenuPacket();
    }

    public static void handle(OpenMenuPacket packet, NetworkEvent.Context context) {
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler::openMenu));
        context.setPacketHandled(true);
    }
}

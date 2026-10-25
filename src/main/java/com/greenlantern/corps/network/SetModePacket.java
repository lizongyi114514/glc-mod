package com.greenlantern.corps.network;

import com.greenlantern.corps.item.RingItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record SetModePacket(int mode) {
    public static void encode(SetModePacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.mode);
    }

    public static SetModePacket decode(FriendlyByteBuf buffer) {
        return new SetModePacket(buffer.readVarInt());
    }

    public static void handle(SetModePacket packet, NetworkEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof RingItem) {
                RingItem.setMode(stack, packet.mode);
            }
        }
        context.setPacketHandled(true);
    }
}

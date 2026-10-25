package com.greenlantern.corps.client;

import com.greenlantern.corps.item.Construct;
import com.greenlantern.corps.item.RingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class HudOverlay implements IGuiOverlay {
    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }
        ItemStack stack = minecraft.player.getMainHandItem();
        if (!RingItem.isRing(stack)) {
            return;
        }
        Font font = minecraft.font;
        int charge = RingItem.getCharge(stack);
        int will = ClientWill.get();
        Construct mode = Construct.byId(RingItem.getMode(stack));
        String text = "充能 " + charge + "   意志 " + will + "   构念 " + mode.display();
        int x = (screenWidth - font.width(text)) / 2;
        int y = screenHeight - 49;
        graphics.drawString(font, text, x + 1, y + 1, 0x000000, false);
        graphics.drawString(font, text, x, y, charge > 0 ? 0x3CFF6E : 0xFF4B3C, false);
    }
}

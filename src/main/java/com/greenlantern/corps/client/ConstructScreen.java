package com.greenlantern.corps.client;

import com.greenlantern.corps.item.Construct;
import com.greenlantern.corps.network.Network;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ConstructScreen extends Screen {
    private static final int PANEL_WIDTH = 380;
    private static final int COLUMN_WIDTH = 180;
    private static final int ROW_HEIGHT = 22;

    public ConstructScreen() {
        super(Component.literal("绿灯军团 · 构念盘"));
    }

    @Override
    protected void init() {
        List<Construct> constructs = new ArrayList<>();
        for (Construct construct : Construct.values()) {
            if (construct != Construct.NONE) {
                constructs.add(construct);
            }
        }
        int left = (this.width - PANEL_WIDTH) / 2;
        int top = this.height / 2 - 96;
        for (int i = 0; i < constructs.size(); i++) {
            Construct construct = constructs.get(i);
            int column = i % 2;
            int row = i / 2;
            addRenderableWidget(Button.builder(
                    Component.literal(construct.display() + " §8" + construct.hint()),
                    button -> {
                        Network.sendMode(construct.id());
                        this.onClose();
                    }).bounds(left + column * COLUMN_WIDTH, top + row * ROW_HEIGHT, COLUMN_WIDTH - 8, 18).build());
        }
        int rows = (constructs.size() + 1) / 2;
        addRenderableWidget(Button.builder(Component.literal("关闭"), button -> this.onClose())
                .bounds(left, top + rows * ROW_HEIGHT + 8, PANEL_WIDTH, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0x99000000);
        graphics.fill((this.width - PANEL_WIDTH) / 2 - 12, this.height / 2 - 118,
                (this.width + PANEL_WIDTH) / 2 + 12, this.height / 2 + 96, 0xCC06140E);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 110, 0x3CFF6E);
        graphics.drawCenteredString(this.font,
                Component.literal("§7右键施放 · 潜行右键再次打开"),
                this.width / 2, this.height / 2 - 96, 0x888888);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

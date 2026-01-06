package com.mkgmod.client;

import com.mkgmod.network.SpaceshipActionPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpaceshipOperatorScreen extends Screen {
    private final BlockPos blockPos;

    public SpaceshipOperatorScreen(BlockPos pos) {
        super(Component.literal("飞船控制台"));
        this.blockPos = pos;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2 - 50;

        // ------------- 按钮 1：组装飞船 -------------
        this.addRenderableWidget(Button.builder(Component.literal("组装飞船"), (button) -> {
            PacketDistributor.sendToServer(new SpaceshipActionPayload(blockPos, "assemble"));
            this.onClose();
        }).bounds(centerX, 60, 100, 20).build());
        // -------------

        // ------------- 按钮 2：发射 -------------
        this.addRenderableWidget(Button.builder(Component.literal("发射飞船"), (button) -> {
            PacketDistributor.sendToServer(new SpaceshipActionPayload(blockPos, "launch"));
            this.onClose();
        }).bounds(centerX, 90, 100, 20).build());
        // -------------
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 0xFFFFFF);
    }
}
package com.mkgmod.client;

import com.mkgmod.network.TeleportPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class VoyagerCompassScreen extends Screen {
    public VoyagerCompassScreen() {
        super(Component.literal("时空罗盘"));
    }

    @Override
    protected void init() {
        int buttonWidth = 100;
        int buttonHeight = 20;
        int centerX = this.width / 2 - buttonWidth / 2;
        int startY = 40; // 第一个按钮的高度
        int spacing = 25; // 按钮之间的间距

        // 按钮 1：传送到主世界
        this.addRenderableWidget(Button.builder(Component.literal("返回主世界"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("minecraft:overworld"));
            this.onClose();
        }).bounds(centerX, startY, buttonWidth, buttonHeight).build());

        // 按钮 2：传送到下界
        this.addRenderableWidget(Button.builder(Component.literal("前往下界"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("minecraft:the_nether"));
            this.onClose();
        }).bounds(centerX, startY + spacing, buttonWidth, buttonHeight).build());

        // 按钮 3：传送到末地
        this.addRenderableWidget(Button.builder(Component.literal("前往末地"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("minecraft:the_end"));
            this.onClose();
        }).bounds(centerX, startY + spacing * 2, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 绘制背景压暗效果
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // 绘制标题
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}
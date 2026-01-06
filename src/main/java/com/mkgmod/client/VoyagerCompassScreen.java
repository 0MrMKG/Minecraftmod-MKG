package com.mkgmod.client;

import com.mkgmod.network.TeleportPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class VoyagerCompassScreen extends Screen {
    public VoyagerCompassScreen() {
        super(Component.literal("旅行者罗盘"));
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
        this.addRenderableWidget(Button.builder(Component.literal("前往地狱"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("minecraft:the_nether"));
            this.onClose();
        }).bounds(centerX, startY + spacing, buttonWidth, buttonHeight).build());

        // 按钮 3：传送到末地
        this.addRenderableWidget(Button.builder(Component.literal("前往末地"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("minecraft:the_end"));
            this.onClose();
        }).bounds(centerX, startY + spacing * 2, buttonWidth, buttonHeight).build());

        // 按钮 4：传送到樱花星球
        this.addRenderableWidget(Button.builder(Component.literal("前往樱花星球"), (button) -> {
            // 发送你的自定义维度 ID
            PacketDistributor.sendToServer(new TeleportPayload("mkgmod:sakura_dimension"));
            this.onClose();
        }).bounds(centerX, startY + spacing * 3, buttonWidth, buttonHeight).build());

        //-------------
        // 按钮 5：创建新星球
        this.addRenderableWidget(Button.builder(Component.literal("创建新星球"), (button) -> {
            // 这里发送用于创建世界的自定义 Payload
            PacketDistributor.sendToServer(new TeleportPayload("action:create_world"));
            this.onClose();
        }).bounds(centerX, startY + spacing * 4, buttonWidth, buttonHeight).build());
        //-------------


    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 绘制背景压暗效果
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        // 绘制标题
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        //-------------
        // 显示已创建星球的总数（这里需要对接你后端保存的世界数量变量，此处以示例数字表示）
        int worldCount = 0; // 替换为获取实际数量的代码，例如：ModData.getWorldCount()
        guiGraphics.drawString(this.font, "已发现星球总数: " + worldCount, 10, this.height - 20, 0xAAAAAA);
        //-------------

    }
}
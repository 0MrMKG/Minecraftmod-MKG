package com.mkgmod.client;

import com.mkgmod.network.TeleportPayload;
import com.mkgmod.render.HolographicButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class VoyagerCompassScreen extends Screen {
    public VoyagerCompassScreen() {
        super(Component.literal("旅行者罗盘"));
    }

//    @Override
//    protected void init() {
//        int buttonWidth = 100;
//        int buttonHeight = 20;
//        int centerX = this.width / 2 - buttonWidth / 2;
//        int startY = 40; // 第一个按钮的高度
//        int spacing = 25; // 按钮之间的间距
//
//        // 按钮 1：传送到主世界
//        this.addRenderableWidget(Button.builder(Component.literal("返回主世界"), (button) -> {
//            PacketDistributor.sendToServer(new TeleportPayload("minecraft:overworld"));
//            this.onClose();
//        }).bounds(centerX, startY, buttonWidth, buttonHeight).build());
//
//        // 按钮 2：传送到下界
//        this.addRenderableWidget(Button.builder(Component.literal("前往地狱"), (button) -> {
//            PacketDistributor.sendToServer(new TeleportPayload("minecraft:the_nether"));
//            this.onClose();
//        }).bounds(centerX, startY + spacing, buttonWidth, buttonHeight).build());
//
//        // 按钮 3：传送到末地
//        this.addRenderableWidget(Button.builder(Component.literal("前往末地"), (button) -> {
//            PacketDistributor.sendToServer(new TeleportPayload("minecraft:the_end"));
//            this.onClose();
//        }).bounds(centerX, startY + spacing * 2, buttonWidth, buttonHeight).build());
//
//        // 按钮 4：传送到樱花星球
//        this.addRenderableWidget(Button.builder(Component.literal("前往樱花星球"), (button) -> {
//            // 发送你的自定义维度 ID
//            PacketDistributor.sendToServer(new TeleportPayload("mkgmod:sakura_dimension"));
//            this.onClose();
//        }).bounds(centerX, startY + spacing * 3, buttonWidth, buttonHeight).build());
//
//        //-------------
//        // 按钮 5：创建新星球
//        this.addRenderableWidget(Button.builder(Component.literal("前往沙漠星球"), (button) -> {
//            // 这里发送用于创建世界的自定义 Payload
//            PacketDistributor.sendToServer(new TeleportPayload("action:create_world"));
//            this.onClose();
//        }).bounds(centerX, startY + spacing * 4, buttonWidth, buttonHeight).build());
//        //-------------
//
//    }

//    @Override
//    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        // 绘制背景压暗效果
//        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
//        super.render(guiGraphics, mouseX, mouseY, partialTick);
//        // 绘制标题
//        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
//
//
//    }

    @Override
    protected void init() {
// --- 1. 计算悬浮窗的坐标（1/2 屏幕大小） ---
        int windowW = this.width / 2;
        int windowH = this.height / 2;
        int windowX = (this.width - windowW) / 2;
        int windowY = (this.height - windowH) / 2;

        // --- 2. 按钮布局参数 ---
        int buttonWidth = 140;
        int buttonHeight = 24;
        int centerX = this.width / 2 - buttonWidth / 2; // 屏幕中心对齐

        // 让第一个按钮从窗口顶部向下偏移 45 像素（为标题留出空间）
        int startY = windowY + 45;
        int spacing = 28; // 按钮间距

        // 按钮 1：返回主世界
        this.addRenderableWidget(new HolographicButton(centerX, startY, buttonWidth, buttonHeight,
                Component.literal("返回主世界"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("minecraft:overworld"));
            this.onClose();
        }));

        // 按钮 2：前往地狱
        this.addRenderableWidget(new HolographicButton(centerX, startY + spacing, buttonWidth, buttonHeight,
                Component.literal("前往地狱"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("minecraft:the_nether"));
            this.onClose();
        }));

        // 按钮 3：前往末地
        this.addRenderableWidget(new HolographicButton(centerX, startY + spacing * 2, buttonWidth, buttonHeight,
                Component.literal("前往末地"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("minecraft:the_end"));
            this.onClose();
        }));

        // 按钮 4：前往樱花星球
        this.addRenderableWidget(new HolographicButton(centerX, startY + spacing * 3, buttonWidth, buttonHeight,
                Component.literal("前往樱花星球"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("mkgmod:sakura_dimension"));
            this.onClose();
        }));

        // 按钮 5：创建新星球
        this.addRenderableWidget(new HolographicButton(centerX, startY + spacing * 4, buttonWidth, buttonHeight,
                Component.literal("前往沙漠星球"), (button) -> {
            PacketDistributor.sendToServer(new TeleportPayload("action:create_world"));
            this.onClose();
        }));
    }


    @Override
    public boolean isPauseScreen() {
        return false; // 返回 false，这样打开界面时时间依然流逝，机器和生物都会动
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 留空！不要调用 super.renderBackground
        // 这样无论是在什么情况下，系统尝试绘制那个黑色遮罩时都会失效
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. 彻底跳过背景暗化
        // 不执行 super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        var pose = guiGraphics.pose();
        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        // 2. 窗口参数定义（原屏幕的 1/2）
        int windowW = this.width / 2;
        int windowH = this.height / 2;
        int left = (this.width - windowW) / 2;
        int top = (this.height - windowH) / 2;

        // 3. 计算 VR 感的旋转系数
        // 将鼠标坐标映射到 -1.0 到 1.0
        float offX = (mouseX - centerX) / centerX;
        float offY = (mouseY - centerY) / centerY;
        pose.pushPose();

        // 4. 应用空间变换
        // 将变换中心设定在屏幕中心
        pose.translate(centerX, centerY, 50); // Z轴 100 产生悬浮感

        // 使用四元数或 Axis 进行 3D 旋转
        // 增加 tiltStrength 使得边缘有明显的厚度感和透视感
        float tiltStrength = 30.0f;
        pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(offX * tiltStrength));
        pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-offY * tiltStrength));

        // 移回中心点开始绘制界面内容
        pose.translate(-centerX, -centerY, 0);

        // 5. 绘制悬浮窗口底板
        // 颜色：极浅的绿色透明感 0x2200FF00
        guiGraphics.fill(left, top, left + windowW, top + windowH, 0x22003300);
        // 绘制高亮边框
        guiGraphics.renderOutline(left, top, windowW, windowH, 0xAA00FF00);

        // 6. 绘制动态扫描线 (VR 特效)
        float scanPos = (System.currentTimeMillis() % 3000) / 3000.0f;
        int scanY = top + (int)(windowH * scanPos);
        guiGraphics.fill(left, scanY, left + windowW, scanY + 1, 0x3300FF00);

        // 7. 渲染标题（带 Z 轴视差）
        pose.pushPose();
        pose.translate(0, 0, 20); // 标题比面板更靠前
        guiGraphics.drawCenteredString(this.font, this.title, (int)centerX, top + 15, 0x00FF00);
        pose.popPose();

        // 8. 渲染子组件（按钮等）
        // 同样受上述 pose 旋转影响，会完美契合在悬浮窗内
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        pose.popPose();

        // 9. 添加环境点缀：屏幕边缘的微弱色差感（可选）
        // renderVignette(guiGraphics);
    }


}
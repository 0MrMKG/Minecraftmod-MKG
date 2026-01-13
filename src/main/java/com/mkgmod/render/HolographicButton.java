package com.mkgmod.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class HolographicButton extends Button {
    private final float scale = 1.2f; // 字号缩放比例

    public HolographicButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();

        // 1. 计算透明度（鼠标悬停时更亮）
        float alpha = this.active ? (this.isHoveredOrFocused() ? 0.4f : 0.2f) : 0.1f;
        int bgColor = (int)(alpha * 255) << 24; // 黑色背景，根据 alpha 变化
        int borderColor = this.isHoveredOrFocused() ? 0xFF00FF00 : 0xAA00AA00; // 亮绿 vs 暗绿边框

        // 2. 绘制半透明背景
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, bgColor);

        // 3. 绘制绿色边框 (1像素)
        guiGraphics.renderOutline(getX(), getY(), width, height, borderColor);

        // 4. 绘制荧光文字（大字号）
        guiGraphics.pose().pushPose();

        // 缩放逻辑：先移动到中心，缩放后再移回来
        float textX = getX() + width / 2.0f;
        float textY = getY() + (height - 8 * scale) / 2.0f;

        guiGraphics.pose().translate(textX, textY, 0);
        guiGraphics.pose().scale(scale, scale, scale);

        // 绘制文字阴影产生“发光”感，颜色使用亮绿色 0x55FF55
        int textColor = this.active ? 0x55FF55 : 0x777777;
        guiGraphics.drawCenteredString(minecraft.font, getMessage(), 0, 0, textColor);

        guiGraphics.pose().popPose();
    }
}
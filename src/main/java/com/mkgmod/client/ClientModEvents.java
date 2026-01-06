package com.mkgmod.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;

@EventBusSubscriber(modid = "mkgmod", value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterEffects(RegisterDimensionSpecialEffectsEvent event) {
        // 创建一个完全自定义的渲染效果
        DimensionSpecialEffects myCustomEffects = new DimensionSpecialEffects(
                Float.NaN,           // 云层高度。设置为 NaN 代表彻底禁用云（原生层级）
                false,               // 是否有地面雾气
                DimensionSpecialEffects.SkyType.END, // 核心！设为 END 会启用末地的天空逻辑
                false,               // 是否强制明亮
                true                 // 是否有恒定的环境光
        ) {
            @Override
            public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
                // 这里可以控制雾气的颜色，brightness 是光照强度
                return fogColor.scale((double)(brightness * 0.94F + 0.06F));
            }

            @Override
            public boolean isFoggyAt(int x, int z) {
                return false;
            }
        };

        // 关键：将你在 JSON 里写的 ID 和这个代码对象绑定起来
        event.register(
                ResourceLocation.fromNamespaceAndPath("mkgmod", "space_void_effects"),
                myCustomEffects
        );
    }
}
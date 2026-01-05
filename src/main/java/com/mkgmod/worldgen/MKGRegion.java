package com.mkgmod.worldgen;

import com.mkgmod.registry.ModBiomes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class MKGRegion extends Region {
    public MKGRegion(ResourceLocation name, int weight) {
        // RegionType.OVERWORLD 表示这是主世界的生成区域
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {

        this.addModifiedVanillaOverworldBiomes(mapper, builder -> {
// --- 极端高山：HIGH_SAKURA_PEAKS ---
            builder.replaceBiome(Climate.parameters(
                            Climate.Parameter.span(-0.2F, 0.2F),   // 温度：稍冷，适合高山樱花
                            Climate.Parameter.span(-0.3F, 0.0F),    // 湿度：湿润，保证樱花茂密
                            // 大陆性设为极高 (0.95 - 1.0)：
                            // 这告诉生成器该区域处于大陆最中心，拥有最大的基础海拔高度。
                            Climate.Parameter.span(0.95F, 1.0F),
                            // 侵蚀度设为最低极端 (-1.2F - -1.0F)：
                            // 这是生成巨大山脉的“核武器”。数值越负，地形越破碎、越陡峭、越高。
                            Climate.Parameter.span(-4.2F, -1.0F),
                            Climate.Parameter.point(0.0F),         // 深度
                            // 奇特度设为极高 (0.8F - 1.2F)：
                            // 极端的 Weirdness 会导致地形产生巨大的突变，形成标志性的“史诗峰顶”。
                            Climate.Parameter.span(0.8F, 1.0F),
                            0.1F),                                 // Offset：稍微增加权重
                    ModBiomes.HIGH_SAKURA_PEAKS
            );

            builder.replaceBiome(Climate.parameters(
                            Climate.Parameter.span(-0.2F, 0.5F), // 温度：温和
                            Climate.Parameter.span(0.0F, 0.5F),  // 湿度：适中
                            Climate.Parameter.span(-0.1F, 0.3F), // 大陆性：较低，靠近海岸或中等陆地
                            Climate.Parameter.span(0.4F, 0.8F),  // 侵蚀度：高，代表非常平坦的地形
                            Climate.Parameter.point(0.0F),       // 深度
                            Climate.Parameter.point(0.0F),       // 奇特度
                            0.0F),
                    ModBiomes.SAKURA_PLAIN
            );

        });
    }

}
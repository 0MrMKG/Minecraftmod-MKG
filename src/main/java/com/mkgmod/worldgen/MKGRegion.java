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
            // 设置一个非常基础的参数：中等温度、中等湿度、中等大陆性
            builder.replaceBiome(Climate.parameters(
                            Climate.Parameter.span(-0.2F, 0.5F), // 温度：稍微放宽以适应更多区域
                            Climate.Parameter.span(0.0F, 0.5F),  // 湿度：樱花通常需要湿润环境
                            Climate.Parameter.span(0.6F, 1.0F),  // 大陆性：设置为极高，代表内陆高原/巨型山脉
                            Climate.Parameter.span(-0.2F, 0.1F), // 侵蚀度：较低但避开极端的负值，形成巨大但不过于尖锐的起伏
                            Climate.Parameter.point(0.0F),       // 深度
                            Climate.Parameter.span(0.0F, 0.3F),  // 奇特度
                            0.0F),
                    ModBiomes.HIGH_SAKURA_PEAKS
            );
        });
    }



}
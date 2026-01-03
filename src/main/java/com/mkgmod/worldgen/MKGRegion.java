package com.mkgmod.worldgen;

import com.mkgmod.registery.ModBiomes;
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
            // 示例：在原本生成“沙漠”的地方，有概率生成你的“塔图因平原”
            // ParameterPoint.of 的参数依次是：温度, 湿度, 大陆性, 侵蚀度, 深度, 奇点
            // 这里我们简单替换沙漠的气候带
            builder.replaceBiome(Biomes.DESERT, ModBiomes.TATOOINE_PLAINS);
            builder.replaceBiome(Biomes.PLAINS, ModBiomes.TATOOINE_PLAINS);
            builder.replaceBiome(Biomes.FOREST, ModBiomes.TATOOINE_PLAINS);
            builder.replaceBiome(Biomes.SAVANNA, ModBiomes.TATOOINE_PLAINS);
            builder.replaceBiome(Biomes.SNOWY_PLAINS, ModBiomes.TATOOINE_PLAINS);
            builder.replaceBiome(Biomes.JUNGLE, ModBiomes.TATOOINE_PLAINS);
        });
    }
}
package com.mkgmod.registery;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class ModBiomes {
    // 这把“钥匙”必须与你的 JSON 文件名（tatooine_plains.json）一致
    public static final ResourceKey<Biome> TATOOINE_PLAINS = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("mkgmod", "tatooine_plains")
    );
}
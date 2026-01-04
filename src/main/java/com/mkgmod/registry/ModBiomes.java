package com.mkgmod.registry;

import com.mkgmod.MKGMOD;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class ModBiomes {

    public static final ResourceKey<Biome> HIGH_SAKURA_PEAKS = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(MKGMOD.MODID, "high_sakura_peaks")
    );


}
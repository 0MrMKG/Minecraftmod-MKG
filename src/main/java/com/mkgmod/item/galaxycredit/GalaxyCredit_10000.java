package com.mkgmod.item.galaxycredit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class GalaxyCredit_10000 extends Item {
    public GalaxyCredit_10000(Properties properties) {
        super(properties
                .fireResistant()                  // 防火防岩浆
                .stacksTo(10)         // 限制堆叠，增加珍贵感
        );
    }
}
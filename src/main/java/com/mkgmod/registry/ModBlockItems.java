package com.mkgmod.registry;

import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("mkgmod");

    public static final DeferredItem<BlockItem> TEST_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.TEST_BLOCK);
    public static final DeferredItem<BlockItem> TEST_BLOCK2_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.TEST_BLOCK2);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

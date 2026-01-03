package com.mkgmod.registery;

import com.mkgmod.MKGMOD;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("mkgmod");

    // ✅ 这是最简洁的写法：直接传入方块的 DeferredHolder
    // 它会自动创建一个 ID 为 "test_block" 的 BlockItem
    public static final DeferredItem<BlockItem> TEST_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.TEST_BLOCK);

    // 如果你有其他普通物品，写在这里
    // public static final DeferredItem<Item> TEST_ITEM = ITEMS.register("test_item", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

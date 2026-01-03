package com.mkgmod.registery;

import com.mkgmod.MKGMOD;
import com.mkgmod.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModBlocks {
    // 创建方块注册表
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MKGMOD.MODID);

    // 注册测试方块
    public static final DeferredBlock<Block> TEST_BLOCK = BLOCKS.register("test_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.0f, 3.0f)
                    .requiresCorrectToolForDrops()
            ));

    // 注册方法
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

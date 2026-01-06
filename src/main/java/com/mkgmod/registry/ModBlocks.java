package com.mkgmod.registry;

import com.mkgmod.MKGMOD;
import com.mkgmod.block.SpaceshipOperatorBlock;
import com.mkgmod.block.TestBlock2;
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

    public static final DeferredBlock<TestBlock2> TEST_BLOCK2 =
            BLOCKS.registerBlock("test_block2",
                    TestBlock2::new,
                    BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops());

    public static final DeferredBlock<SpaceshipOperatorBlock> SPACESHIP_OPERATOR_Block =
            BLOCKS.registerBlock("spaceship_operator_block",
                    SpaceshipOperatorBlock::new,
                    BlockBehaviour.Properties.of().strength(3.0f).requiresCorrectToolForDrops());


    // 注册方法
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

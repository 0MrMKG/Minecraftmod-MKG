package com.mkgmod.init;

import com.mkgmod.MKGMOD;
import com.mkgmod.block.TestBlock2;
import com.mkgmod.blockentity.TestBlock2Entity;
import com.mkgmod.registery.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.mkgmod.registery.ModBlocks.BLOCKS;

public class ModBlockEntities {
    // 创建延迟注册器
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "mkgmod");

    // 注册 TestBlock2 的实体类型
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TestBlock2Entity>> TEST_BLOCK2_ENTITY =
            BLOCK_ENTITIES.register("test_block2_entity",
                    () -> BlockEntityType.Builder.of(TestBlock2Entity::new, ModBlocks.TEST_BLOCK2.get()).build(null));
}
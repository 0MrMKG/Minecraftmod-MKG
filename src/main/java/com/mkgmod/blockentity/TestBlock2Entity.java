package com.mkgmod.blockentity;

import com.mkgmod.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TestBlock2Entity extends BlockEntity {
    public TestBlock2Entity(BlockPos pos, BlockState state) {
        // 这里的 ModBlockEntities.TEST_BLOCK2_ENTITY 是你在注册类中定义的 RegistryObject
        super(ModBlockEntities.TEST_BLOCK2_ENTITY.get(), pos, state);
    }
}
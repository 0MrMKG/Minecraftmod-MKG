package com.mkgmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class TestBlock extends Block {
    public TestBlock() {
        super(BlockBehaviour.Properties
                .of()
                .strength(5.0F));
    }

}
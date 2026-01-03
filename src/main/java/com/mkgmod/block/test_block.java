package com.mkgmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;


public class test_block extends Block {
    public test_block() {
        super(BlockBehaviour.Properties
                .of()
                .strength(5.0F));
    }

}
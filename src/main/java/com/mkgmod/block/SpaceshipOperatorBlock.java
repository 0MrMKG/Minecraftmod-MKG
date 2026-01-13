package com.mkgmod.block;

import com.mkgmod.blockentity.SpaceshipOperatorBlockEntity;
import com.mkgmod.blockentity.TestBlock2Entity;
import com.mkgmod.client.SpaceshipOperatorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;


public class SpaceshipOperatorBlock extends Block implements EntityBlock {
    public SpaceshipOperatorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpaceshipOperatorBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(new SpaceshipOperatorScreen(pos));
        }
        return InteractionResult.SUCCESS;
    }
}
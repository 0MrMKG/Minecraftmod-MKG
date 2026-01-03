package com.mkgmod.block;

import com.mkgmod.blockentity.TestBlock2Entity;
import com.mkgmod.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class TestBlock2 extends Block implements EntityBlock {
    public TestBlock2(Properties properties) {
        super(properties);
    }

    // 实现 EntityBlock 接口，将方块与实体类绑定
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TestBlock2Entity(pos, state);
    }

    // 处理右键点击逻辑
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) { // 只在服务器端处理逻辑，防止消息打印两次
            player.sendSystemMessage(Component.literal("你点击了实体方块 TestBlock2！"));
        }
        return InteractionResult.SUCCESS;
    }
}
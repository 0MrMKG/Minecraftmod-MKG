package com.mkgmod.item.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CreditCollector extends Item {

    public CreditCollector(Properties properties) {
        super(properties);
    }

    /**
     * 当玩家右键点击空气或方块时触发
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // 这里的 !level.isClientSide 是关键：
        // 只有在服务器逻辑侧运行，才打印文字并执行后续的货币逻辑（防止逻辑重复运行或数据不一致）
        if (!level.isClientSide) {

            // 在玩家聊天框中显示文字
            player.sendSystemMessage(Component.literal("§6[星际终端] §f正在扫描背包中的信用点..."));

            // 同时在控制台打印一条调试信息
            System.out.println("玩家 " + player.getName().getString() + " 触发了信用点收集逻辑");

        }

        // 返回成功，并让玩家的手臂摆动
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
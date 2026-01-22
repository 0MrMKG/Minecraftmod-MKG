package com.mkgmod.item.tool;


import com.mkgmod.item.ModItems;
import com.mkgmod.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class CreditCollector extends Item {

    // 使用 Map 映射道具与其对应的面值
    private static final Map<Item, Integer> CREDIT_VALUES = new HashMap<>();

    public CreditCollector(Properties properties) {
        super(properties);
        // 初始化面值映射（建议在构造函数或静态块中完成）
        // 这里需要替换为你真实的物品注册名

        CREDIT_VALUES.put(ModItems.GALAXY_CREDIT_1.get(), 1);
        CREDIT_VALUES.put(ModItems.GALAXY_CREDIT_10.get(), 10);
        CREDIT_VALUES.put(ModItems.GALAXY_CREDIT_100.get(), 100);
        CREDIT_VALUES.put(ModItems.GALAXY_CREDIT_1000.get(), 1000);
        CREDIT_VALUES.put(ModItems.GALAXY_CREDIT_10000.get(), 10000);

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            long totalFound = 0;

            // 1. 遍历玩家背包（包括快捷栏和主背包）
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                Item item = stack.getItem();

                // 2. 检查是否是信用点
                if (CREDIT_VALUES.containsKey(item)) {
                    int valuePerItem = CREDIT_VALUES.get(item);
                    int count = stack.getCount();

                    totalFound += (long) valuePerItem * count;

                    // 3. 消耗背包里的道具
                    stack.setCount(0);
                }
            }

            if (totalFound > 0) {
                // 4. 将金额存入玩家数据 (假设你使用了 Attachment)
                player.getData(ModAttachments.PLAYER_CREDITS).addCredits((int)totalFound);

                player.sendSystemMessage(Component.literal("§6[星际终端] §a做的好，信用点+§e" + totalFound));
                player.sendSystemMessage(Component.literal("§6[星际终端] §f当前总账户余额已更新。"));
            } else {
                player.sendSystemMessage(Component.literal("§c[星际终端] 未在背包中检测到可识别的信用点数据"));
            }
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}